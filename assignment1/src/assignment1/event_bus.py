import inspect
import os
import re
from collections.abc import Awaitable, Callable
from typing import Any

import aio_pika
from aio_pika import DeliveryMode, ExchangeType, Message
from aio_pika.abc import (
    AbstractIncomingMessage,
    AbstractRobustChannel,
    AbstractRobustConnection,
    AbstractRobustExchange,
    AbstractRobustQueue,
)
from bubus import EventBus

RABBITMQ_URL = "amqp://guest:guest@127.0.0.1/"
EVENT_EXCHANGE_NAME = "events"


class EventBusWrapper:
    def __init__(self, bus: EventBus | None, actor_name: str):
        self.bus = bus
        self.actor_name = actor_name
        self._rabbitmq_bus: RabbitMQEventBus | None = None

    async def dispatch(self, event: Any) -> None:
        if self.bus is not None:
            self.bus.dispatch(event)
            return

        rabbitmq_bus = await self._get_rabbitmq_bus()
        await rabbitmq_bus.dispatch(event)

    async def subscribe(
        self, event_type: type[Any], handler: Callable[[Any], Any]
    ) -> None:
        if self.bus is not None:
            self.bus.on(event_type, handler)
            return

        rabbitmq_bus = await self._get_rabbitmq_bus()
        await rabbitmq_bus.subscribe(event_type, handler, self.actor_name)

    async def close(self) -> None:
        if self._rabbitmq_bus is None:
            return
        await self._rabbitmq_bus.close()

    async def _get_rabbitmq_bus(self) -> "RabbitMQEventBus":
        if self._rabbitmq_bus is None:
            self._rabbitmq_bus = RabbitMQEventBus()
            await self._rabbitmq_bus.connect()
        return self._rabbitmq_bus


class RabbitMQEventBus:
    def __init__(self, url: str = RABBITMQ_URL):
        self.url = url
        self.connection: AbstractRobustConnection | None = None
        self.channel: AbstractRobustChannel | None = None
        self.exchange: AbstractRobustExchange | None = None
        self.queues: list[AbstractRobustQueue] = []

    async def connect(self) -> None:
        if self.connection is not None:
            return

        self.connection = await aio_pika.connect_robust(self.url)
        self.channel = await self.connection.channel()
        self.exchange = await self.channel.declare_exchange(
            EVENT_EXCHANGE_NAME,
            ExchangeType.TOPIC,
            durable=True,
        )

    async def dispatch(self, event: Any) -> None:
        await self.connect()
        assert self.exchange is not None

        message = Message(
            body=event.model_dump_json().encode(),
            content_type="application/json",
            delivery_mode=DeliveryMode.PERSISTENT,
            type=event.__class__.__name__,
        )
        await self.exchange.publish(message, routing_key=event.__class__.__name__)

    async def subscribe(
        self,
        event_type: type[Any],
        handler: Callable[[Any], Any],
        actor_name: str,
    ) -> None:
        await self.connect()

        queue_name = build_queue_name(actor_name, event_type.__name__)
        queue = await self.channel.declare_queue(
            queue_name,
            exclusive=False,
            auto_delete=True,
        )
        await queue.bind(self.exchange, routing_key=event_type.__name__)
        await queue.consume(self._build_consumer(event_type, handler))
        self.queues.append(queue)

    def _build_consumer(
        self,
        event_type: type[Any],
        handler: Callable[[Any], Any],
    ) -> Callable[[AbstractIncomingMessage], Awaitable[None]]:
        async def consume(message: AbstractIncomingMessage) -> None:
            async with message.process():
                event = event_type.model_validate_json(message.body.decode())
                result = handler(event)
                if inspect.isawaitable(result):
                    await result

        return consume

    async def close(self) -> None:
        for queue in self.queues:
            await queue.delete(if_unused=False, if_empty=False)
        self.queues.clear()

        if self.channel is not None:
            await self.channel.close()
            self.channel = None

        if self.connection is not None:
            await self.connection.close()
            self.connection = None

        self.exchange = None


def build_queue_name(actor_name: str, event_name: str) -> str:
    normalized_actor_name = actor_name.replace(" ", "-").lower()
    normalized_event_name = event_name.replace(" ", "-").lower()
    return f"assignment1.{normalized_actor_name}.{normalized_event_name}"

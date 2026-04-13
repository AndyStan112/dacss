import asyncio

from assignment1.event_bus import EventBusWrapper
from assignment1.events import TrafficEvent
from bubus import EventBus


class NavigationApp:
    def __init__(self, name: str, bus: EventBus):
        self.name = name
        self.event_bus = EventBusWrapper(bus, f"navigation-app-{name}")

    async def register_handlers(self):
        async def handler(event: TrafficEvent):
            print(
                f"Navigation app '{self.name}' was informed of congestion in {event.area} on {event.road}"
            )

        await self.event_bus.subscribe(TrafficEvent, handler)

    async def run(self):
        try:
            await self.register_handlers()
            while 1:
                await asyncio.sleep(10)
        finally:
            await self.event_bus.close()

import asyncio

from assignment1.event_bus import EventBusWrapper
from assignment1.events import (
    BehaviourFineEvent,
    PublicEvent,
    SpeedingFineEvent,
    SpeedingEvent,
)
from bubus import EventBus


class Police:
    def __init__(self, bus: EventBus, area):
        self.area = area
        self.event_bus = EventBusWrapper(bus, f"police-{area}")

    async def publish_speeding_fine(self, event: SpeedingEvent):
        await self.event_bus.dispatch(
            SpeedingFineEvent(area=event.area, speed=event.speed)
        )

    async def publish_behaviour_fine(self, event: PublicEvent):
        await self.event_bus.dispatch(
            BehaviourFineEvent(area=event.area, behaviour=event.behaviour)
        )

    async def register_handlers(self):
        async def speeding_handler(event: SpeedingEvent):
            print(
                f"Police in {self.area} was informed of speeding in {event.area} at {event.speed} km/h"
            )
            if event.area != self.area:
                return
            if event.speed - event.speed_limit < 10:
                print(
                    f"Police in {self.area} did not issue a fine for speeding at {event.speed} km/h"
                )
                return
            await self.publish_speeding_fine(event)

        async def behaviour_handler(event: PublicEvent):
            print(
                f"Police in {self.area} was informed of {event.behaviour} behaviour in {event.area}"
            )
            if event.area != self.area:
                return
            await self.publish_behaviour_fine(event)

        await self.event_bus.subscribe(PublicEvent, behaviour_handler)
        await self.event_bus.subscribe(SpeedingEvent, speeding_handler)

    async def run(self):
        try:
            await self.register_handlers()
            while 1:
                await asyncio.sleep(10)
        finally:
            await self.event_bus.close()

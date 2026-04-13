import asyncio
import random

from assignment1.event_bus import EventBusWrapper
from assignment1.events import PublicEvent


class PublicCamera:
    def __init__(self, bus, area: str):
        self.area = area
        self.event_bus = EventBusWrapper(bus, f"public-camera-{area}")

    async def publish(self, behaviour: str):
        await self.event_bus.dispatch(PublicEvent(area=self.area, behaviour=behaviour))

    async def run(self):
        try:
            while 1:
                await asyncio.sleep(5)
                behaviour = random.choice(["normal", "trash", "crime"])
                if behaviour != "normal":
                    await self.publish(behaviour)
        finally:
            await self.event_bus.close()

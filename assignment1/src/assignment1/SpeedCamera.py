import asyncio
import random

from assignment1.event_bus import EventBusWrapper
from assignment1.events import SpeedingEvent


class SpeedCamera:
    def __init__(self, bus, speed_limit: int = 100, area: str = "Complex"):
        self.speed_limit = speed_limit
        self.area = area
        self.event_bus = EventBusWrapper(bus, f"speed-camera-{area}")

    async def publish(self, speed: int):
        await self.event_bus.dispatch(
            SpeedingEvent(area=self.area, speed=speed, speed_limit=self.speed_limit)
        )

    async def run(self):
        try:
            while 1:
                await asyncio.sleep(2)
                speed = random.randint(20, 120)
                if speed > self.speed_limit:
                    await self.publish(speed)
        finally:
            await self.event_bus.close()

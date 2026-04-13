import asyncio
import random

from assignment1.event_bus import EventBusWrapper
from assignment1.events import TrafficEvent
from bubus import EventBus


class TrafficCamera:
    def __init__(self, bus: EventBus, area: str, road: str):
        self.area = area
        self.road = road
        self.event_bus = EventBusWrapper(bus, f"traffic-camera-{area}-{road}")

    async def publish(self):
        await self.event_bus.dispatch(TrafficEvent(area=self.area, road=self.road))

    async def run(self):
        try:
            while 1:
                await asyncio.sleep(5)
                idk = random.randint(1, 10)
                if idk > 5:
                    await self.publish()
        finally:
            await self.event_bus.close()

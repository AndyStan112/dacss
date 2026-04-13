import asyncio
import random

from assignment1.events import TrafficEvent
from bubus import EventBus


class TrafficCamera:
    def __init__(self, bus: EventBus, area: str, road: str):
        self.area = area
        self.road = road
        self.bus = bus

    def publish(self):
        if self.bus is None:
            return
        self.bus.dispatch(TrafficEvent(area=self.area, road=self.road))

    async def run(self):
        while 1:
            await asyncio.sleep(5)
            idk = random.randint(1, 10)
            if idk > 5:
                self.publish()

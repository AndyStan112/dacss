import asyncio
import random
from assignment1.events import PublicEvent


class PublicCamera:
    def __init__(self, bus, area: str):
        self.area = area
        self.bus = bus

    def publish(self, behaviour: str):
        if self.bus is None:
            return
        self.bus.dispatch(PublicEvent(area=self.area, behaviour=behaviour))

    async def run(self):
        while 1:
            await asyncio.sleep(5)
            behaviour = random.choice(["normal", "trash", "crime"])
            if behaviour != "normal":
                self.publish(behaviour)

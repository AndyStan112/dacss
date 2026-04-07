import asyncio
import random
from assignment1.events import SpeedingEvent
class SpeedCamera:
    def __init__(self,bus, speed_limit:int = 100, area:str = "Complex"):
        self.speed_limit = speed_limit
        self.area = area
        self.bus = bus
    def publish(self, speed:int):
        self.bus.dispatch(SpeedingEvent(area=self.area, speed=speed))

    async def run(self):
        while(1):
            await asyncio.sleep(2)
            speed = random.randint(20, 120)
            if(speed > self.speed_limit):
                self.publish(speed)


    
    
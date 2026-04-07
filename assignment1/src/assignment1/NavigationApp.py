import asyncio
import random
import uuid

from assignment1.events import TrafficEvent
from bubus import EventBus


class NavigationApp:
    def __init__(self,name:str,bus:EventBus):
        self.name = name
        self.bus = bus


    async def run(self):
        async def handler(event:TrafficEvent):
            print(f"Navigation app '{self.name}' was informed of congestion in {event.area} on {event.road}")
        self.bus.on(TrafficEvent, handler)
        while(1):
            await asyncio.sleep(10)

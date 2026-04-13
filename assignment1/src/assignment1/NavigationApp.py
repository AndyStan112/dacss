import asyncio

from assignment1.events import TrafficEvent
from bubus import EventBus


class NavigationApp:
    def __init__(self, name: str, bus: EventBus):
        self.name = name
        self.bus = bus

    def register_handlers(self):
        if self.bus is None:
            return

        async def handler(event: TrafficEvent):
            print(
                f"Navigation app '{self.name}' was informed of congestion in {event.area} on {event.road}"
            )

        self.bus.on(TrafficEvent, handler)

    async def run(self):
        self.register_handlers()
        while 1:
            await asyncio.sleep(10)

import asyncio

from assignment1.event_bus import EventBusWrapper
from assignment1.events import BehaviourFineEvent, SpeedingFineEvent


class Dashboard:
    def __init__(self, bus):
        self.event_bus = EventBusWrapper(bus, "dashboard")
        self.fines = {}
        self.lock = asyncio.Lock()

    async def register_handlers(self):
        async def speeding_handler(event: SpeedingFineEvent):
            async with self.lock:
                if event.area not in self.fines:
                    self.fines[event.area] = {"speeding": 0, "behaviour": 0}
                self.fines[event.area]["speeding"] += 1

        async def behaviour_handler(event: BehaviourFineEvent):
            async with self.lock:
                if event.area not in self.fines:
                    self.fines[event.area] = {"speeding": 0, "behaviour": 0}
                self.fines[event.area]["behaviour"] += 1

        await self.event_bus.subscribe(BehaviourFineEvent, behaviour_handler)
        await self.event_bus.subscribe(SpeedingFineEvent, speeding_handler)

    async def run(self):
        try:
            await self.register_handlers()
            while 1:
                await asyncio.sleep(10)
                print("Dashboard: Fines issued per area:")
                async with self.lock:
                    for area, count in self.fines.items():
                        print(f"{area}: {count} fines")
        finally:
            await self.event_bus.close()

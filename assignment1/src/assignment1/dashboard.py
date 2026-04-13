import asyncio

from assignment1.events import BehaviourFineEvent, SpeedingFineEvent


class Dashboard:
    def __init__(self, bus):
        self.bus = bus
        self.fines = {}
        self.lock = asyncio.Lock()

    def register_handlers(self):
        if self.bus is None:
            return

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

        self.bus.on(BehaviourFineEvent, behaviour_handler)
        self.bus.on(SpeedingFineEvent, speeding_handler)

    async def run(self):
        self.register_handlers()
        while 1:
            await asyncio.sleep(10)
            print("Dashboard: Fines issued per area:")
            async with self.lock:
                for area, count in self.fines.items():
                    print(f"{area}: {count} fines")

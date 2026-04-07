import asyncio


from assignment1.events import BehaviourFineEvent, PublicEvent, SpeedingFineEvent, SpeedingEvent
from bubus import EventBus


class Police:
    def __init__(self,bus:EventBus,area):
        self.area = area
        self.bus = bus


    async def run(self):
        async def speeding_handler(event:SpeedingEvent):
            print(f"Police in {self.area} was informed of speeding in {event.area} at {event.speed} km/h")
            if event.area != self.area:
                return
            if event.speed - event.speed_limit < 10:
                print(f"Police in {self.area} did not issue a fine for speeding at {event.speed} km/h")
                return
            self.bus.dispatch(SpeedingFineEvent(area=event.area, speed=event.speed))

        async def behaviour_handler(event:PublicEvent):
            print(f"Police in {self.area} was informed of {event.behaviour} behaviour in {event.area}")
            if event.area != self.area:
                return
            self.bus.dispatch(BehaviourFineEvent(area=event.area, behaviour=event.behaviour))
                
        self.bus.on(PublicEvent, behaviour_handler)
        self.bus.on(SpeedingEvent, speeding_handler)
        while(1):
            await asyncio.sleep(10)

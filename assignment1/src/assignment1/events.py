from bubus import BaseEvent


class TrafficEvent(BaseEvent[None]):
    area: str
    road: str

class SpeedingEvent(BaseEvent[None]):
    area: str
    speed: int
    speed_limit: int

class PublicEvent(BaseEvent[None]):
    area: str
    behaviour: str

class FineEvent(BaseEvent[None]):
    area: str

class SpeedingFineEvent(FineEvent):
    area: str
    speed: int

class BehaviourFineEvent(FineEvent):
    area: str
    behaviour: str
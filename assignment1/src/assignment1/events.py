from bubus import BaseEvent


class TrafficEvent(BaseEvent[None]):
    area: str
    road: str

class SpeedingEvent(BaseEvent[None]):
    area: str
    speed: int

class PublicEvent(BaseEvent[None]):
    area: str
    behaviour: str

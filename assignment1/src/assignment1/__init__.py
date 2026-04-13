import asyncio
from assignment1.Police import Police
from assignment1.dashboard import Dashboard
from bubus import EventBus, BaseEvent
from assignment1.SpeedCamera import SpeedCamera
from assignment1.TrafficCamera import TrafficCamera
from assignment1.PublicCamera import PublicCamera
from assignment1.NavigationApp import NavigationApp


async def main() -> None:
    bus = EventBus()

    speed_cameras = [
        SpeedCamera(bus, speed_limit=80, area="Complex").run(),
        SpeedCamera(bus, speed_limit=50, area="Fabric").run(),
    ]
    traffic_cameras = [
        TrafficCamera(bus, "Complex", "Eroilor").run(),
        TrafficCamera(bus, "Complex", "Dianei").run(),
        TrafficCamera(bus, "Fabric", "Fabrica de Bere").run(),
    ]

    public_cameras = [
        PublicCamera(bus, "Complex").run(),
        PublicCamera(bus, "Fabric").run(),
    ]

    apps = [NavigationApp("wayz", bus).run(), NavigationApp("google maps", bus).run()]

    police = [Police(bus, "Complex").run(), Police(bus, "Fabric").run()]

    dashboard = Dashboard(bus).run()

    await asyncio.gather(
        *speed_cameras, *traffic_cameras, *public_cameras, *apps, *police, dashboard
    )


asyncio.run(main())

import asyncio
import sys
from multiprocessing import Process

from assignment1.NavigationApp import NavigationApp
from assignment1.Police import Police
from assignment1.PublicCamera import PublicCamera
from assignment1.SpeedCamera import SpeedCamera
from assignment1.TrafficCamera import TrafficCamera
from assignment1.dashboard import Dashboard
from bubus import EventBus


def build_actors(bus):
    speed_cameras = [
        SpeedCamera(bus, speed_limit=80, area="Complex"),
        SpeedCamera(bus, speed_limit=50, area="Fabric"),
    ]
    traffic_cameras = [
        TrafficCamera(bus, "Complex", "Eroilor"),
        TrafficCamera(bus, "Complex", "Dianei"),
        TrafficCamera(bus, "Fabric", "Fabrica de Bere"),
    ]

    public_cameras = [
        PublicCamera(bus, "Complex"),
        PublicCamera(bus, "Fabric"),
    ]

    apps = [NavigationApp("wayz", bus), NavigationApp("google maps", bus)]

    police = [Police(bus, "Complex"), Police(bus, "Fabric")]

    dashboard = Dashboard(bus)

    return [
        *speed_cameras,
        *traffic_cameras,
        *public_cameras,
        *apps,
        *police,
        dashboard,
    ]


async def run_inprocess() -> None:
    bus = EventBus()
    actors = build_actors(bus)
    await asyncio.gather(*(actor.run() for actor in actors))


def _run_component(component) -> None:
    asyncio.run(component.run())


def main() -> None:
    if sys.argv[1] == "local":
        asyncio.run(run_inprocess())
    elif sys.argv[1] == "process":
        run_multiprocess()
    else:
        print("ooopsie")
        sys.exit(1)


def run_multiprocess() -> None:
    actors = build_actors(None)
    processes = [
        Process(
            target=_run_component,
            name=f"{actor.__class__.__name__}-{index}",
            args=(actor,),
        )
        for index, actor in enumerate(actors)
    ]

    try:
        for process in processes:
            process.start()

        for process in processes:
            process.join()
    except KeyboardInterrupt:
        for process in processes:
            if process.is_alive():
                process.terminate()
    finally:
        for process in processes:
            process.join()

import asyncio
import random

from dbus_next.aio import MessageBus
from dbus_next.service import ServiceInterface, method


class WeatherService(ServiceInterface):
    def __init__(self):
        super().__init__("com.fake.Weather")
        self._conditions = ["Sunny", "Cloudy", "Rainy", "Snowy", "Windy", "Foggy"]

    @method()
    def GetWeather(self) -> "s":
        temp = round(random.uniform(-10, 40), 1)
        condition = random.choice(self._conditions)
        humidity = random.randint(20, 100)
        return f"{condition}, {temp}°C, {humidity}% humidity"


async def main():
    bus = await MessageBus().connect()
    interface = WeatherService()
    bus.export("/com/fake/Weather", interface)
    await bus.request_name("com.fake.Weather")
    print("Weather service running on D-Bus (com.fake.Weather)")
    await asyncio.get_event_loop().create_future()  # run forever


if __name__ == "__main__":
    asyncio.run(main())

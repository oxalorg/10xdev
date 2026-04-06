import asyncio

from dbus_next.aio import MessageBus


async def main():
    bus = await MessageBus().connect()
    introspection = await bus.introspect("com.fake.Weather", "/com/fake/Weather")
    proxy = bus.get_proxy_object("com.fake.Weather", "/com/fake/Weather", introspection)
    interface = proxy.get_interface("com.fake.Weather")

    print("Fetching weather every 5 seconds...")
    while True:
        weather = await interface.call_get_weather()
        print(f"Weather: {weather}")
        await asyncio.sleep(5)


if __name__ == "__main__":
    asyncio.run(main())

package interopSample.mappers

import interopSample.ktor.dtos.WeatherRemote
import interopSample.models.Weather

internal fun WeatherRemote.toDomain() = Weather(
    temperature = currentWeather.temperature,
    temperatureUnit = currentWeatherUnits.temperature
)
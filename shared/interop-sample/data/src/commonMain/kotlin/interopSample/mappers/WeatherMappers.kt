package interopSample.mappers

import interopSample.cloud.dtos.WeatherDTO
import interopSample.models.Weather

internal fun WeatherDTO.toDomain() = Weather(
    temperature = currentWeather.temperature,
    temperatureUnit = currentWeatherUnits.temperature
)
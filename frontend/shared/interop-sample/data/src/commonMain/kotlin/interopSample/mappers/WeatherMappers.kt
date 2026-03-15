package interopSample.mappers

import data.WeatherEntity
import interopSample.cloud.dtos.WeatherDTO
import interopSample.models.Weather

internal fun WeatherDTO.toDomain() = Weather(
    temperature = currentWeather.temperature+(1..10).random(),
    temperatureUnit = currentWeatherUnits.temperature
)

internal fun WeatherEntity.toDomain() = Weather(
    temperature = this.temperature,
    temperatureUnit = this.unit
)
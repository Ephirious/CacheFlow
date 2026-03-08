package interopSample.ktor.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WeatherRemote(
    @SerialName("current_weather")
    val currentWeather: CurrentWeather,
    @SerialName("current_weather_units")
    val currentWeatherUnits: CurrentWeatherUnits
)

@Serializable
internal data class CurrentWeather(
    @SerialName("temperature")
    val temperature: Float
)

@Serializable
internal data class CurrentWeatherUnits(
    @SerialName("temperature")
    val temperature: String
)
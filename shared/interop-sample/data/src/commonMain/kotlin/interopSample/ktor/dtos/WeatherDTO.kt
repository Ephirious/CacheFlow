package interopSample.ktor.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WeatherDTO(
    @SerialName("current_weather")
    val currentWeather: CurrentWeatherDTO,
    @SerialName("current_weather_units")
    val currentWeatherUnits: CurrentWeatherUnitsDTO
) {
    @Serializable
    internal data class CurrentWeatherDTO(
        @SerialName("temperature")
        val temperature: Float
    )

    @Serializable
    internal data class CurrentWeatherUnitsDTO(
        @SerialName("temperature")
        val temperature: String
    )
}


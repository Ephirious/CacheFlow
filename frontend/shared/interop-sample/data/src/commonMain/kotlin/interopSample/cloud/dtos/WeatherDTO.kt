package interopSample.cloud.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.types.BigDecimal

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
        val temperature: BigDecimal,
    )

    @Serializable
    internal data class CurrentWeatherUnitsDTO(
        @SerialName("temperature")
        val temperature: String
    )
}


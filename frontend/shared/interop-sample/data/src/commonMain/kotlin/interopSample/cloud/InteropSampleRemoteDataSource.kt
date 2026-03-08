package interopSample.cloud

import interopSample.cloud.dtos.WeatherDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*

internal class InteropSampleRemoteDataSource(
    private val httpClient: HttpClient
) {
    private val openMeteoUrl = buildUrl {
        protocol = URLProtocol.HTTPS
        host = "api.open-meteo.com"
        path("v1", "forecast")
        parameters.appendAll("latitude" to "55.7558", "longitude" to "37.6173", "current_weather" to "true")
    }

    suspend fun fetchWeather(): WeatherDTO = runCatching {
        httpClient.get(openMeteoUrl).body<WeatherDTO>()
    }.fold(
        onSuccess = { response -> response },
        onFailure = { error -> throw (error as? Exception ?: RuntimeException(error)) }
    )
}
package interopSample.repositories

import interopSample.models.Weather
import kotlinx.coroutines.flow.Flow

interface InteropSampleRepository {

    fun getWeatherFlow(): Flow<Weather>
    suspend fun getWeather(): Weather
    suspend fun refreshWeather()

    fun setSampleText(text: String)
    fun getSampleText(): String
}
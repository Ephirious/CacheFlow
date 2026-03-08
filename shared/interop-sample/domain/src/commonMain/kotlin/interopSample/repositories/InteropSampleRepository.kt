package interopSample.repositories

import interopSample.models.Weather

interface InteropSampleRepository {
    suspend fun getWeather(fromNetwork: Boolean): Weather

    fun setSampleText(text: String)
    fun getSampleText(): String
}
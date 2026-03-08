package interopSample.usecases

import interopSample.models.Weather
import interopSample.repositories.InteropSampleRepository

class GetWeatherUseCase(
    private val repository: InteropSampleRepository
) {
    suspend operator fun invoke(fromNetwork: Boolean): Weather {
        return repository.getWeather(fromNetwork)
    }
}
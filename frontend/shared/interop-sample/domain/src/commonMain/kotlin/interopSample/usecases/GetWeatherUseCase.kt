package interopSample.usecases

import interopSample.repositories.InteropSampleRepository

class GetWeatherUseCase(
    private val repository: InteropSampleRepository
) {
    suspend operator fun invoke() = repository.getWeather()
}
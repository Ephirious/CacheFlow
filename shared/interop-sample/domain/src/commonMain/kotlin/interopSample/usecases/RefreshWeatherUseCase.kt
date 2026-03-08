package interopSample.usecases

import interopSample.repositories.InteropSampleRepository

class RefreshWeatherUseCase(
    private val repository: InteropSampleRepository
) {
    suspend operator fun invoke() {
        repository.refreshWeather()
    }
}
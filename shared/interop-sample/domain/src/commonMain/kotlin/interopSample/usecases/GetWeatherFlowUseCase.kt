package interopSample.usecases

import interopSample.repositories.InteropSampleRepository

class GetWeatherFlowUseCase(
    private val repository: InteropSampleRepository
) {
    operator fun invoke() = repository.getWeatherFlow()
}
package interopSample.usecases

import interopSample.repositories.InteropSampleRepository

class ManageSampleTextUseCases(
    private val repository: InteropSampleRepository
) {
    fun setSampleText(text: String) = repository.setSampleText(text)
    fun getSampleText(): String = repository.getSampleText()
}
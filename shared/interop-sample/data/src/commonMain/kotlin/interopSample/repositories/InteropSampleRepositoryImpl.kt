package interopSample.repositories

import interopSample.cloud.InteropSampleRemoteDataSource
import interopSample.local.InteropSampleLocalDataSource
import interopSample.mappers.toDomain
import interopSample.models.Weather

internal class InteropSampleRepositoryImpl(
    private val remoteDataSource: InteropSampleRemoteDataSource,
    private val localDataSource: InteropSampleLocalDataSource,
) : InteropSampleRepository {
    override suspend fun getWeather(fromNetwork: Boolean): Weather =
        if (fromNetwork) {
            remoteDataSource.fetchWeather().toDomain()
        } else {
            error("Здесь должны были быть оффлайн данные =)")
        }

    override fun setSampleText(text: String) = localDataSource.setSampleText(text)
    override fun getSampleText(): String = localDataSource.getSampleText()

}
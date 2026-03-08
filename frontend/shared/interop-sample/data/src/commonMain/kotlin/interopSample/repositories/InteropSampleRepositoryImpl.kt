package interopSample.repositories

import interopSample.cloud.InteropSampleRemoteDataSource
import interopSample.db.InteropSampleDatabaseDataSource
import interopSample.local.InteropSampleLocalDataSource
import interopSample.mappers.toDomain
import interopSample.models.Weather
import kotlinx.coroutines.flow.Flow

internal class InteropSampleRepositoryImpl(
    private val remoteDataSource: InteropSampleRemoteDataSource,
    private val localDataSource: InteropSampleLocalDataSource,
    private val dbDataSource: InteropSampleDatabaseDataSource,
) : InteropSampleRepository {
    override fun getWeatherFlow(): Flow<Weather> =
        dbDataSource.getWeatherFlow()

    override suspend fun refreshWeather() {
        runCatching {
            remoteDataSource.fetchWeather()
        }.fold(
            onSuccess = { dto ->
                dbDataSource.saveWeather(dto.toDomain())

                dbDataSource.getWeather()
            },
            onFailure = { throw it }
        )
    }

    override fun setSampleText(text: String) = localDataSource.setSampleText(text)
    override fun getSampleText(): String = localDataSource.getSampleText()

}
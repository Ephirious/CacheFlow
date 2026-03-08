package interopSample.repositories

import interopSample.ktor.InteropSampleRemoteDataSource
import interopSample.mappers.toDomain
import interopSample.models.Weather

internal class InteropSampleRepositoryImpl(
    private val remoteDataSource: InteropSampleRemoteDataSource,
) : InteropSampleRepository {
    override suspend fun getWeather(fromNetwork: Boolean): Weather =
        if (fromNetwork) {
            remoteDataSource.fetchWeather().toDomain()
        } else {
            error("Здесь должны были быть оффлайн данные =)")
        }
}
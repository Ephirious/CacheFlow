package interopSample.db

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import data.WeatherQueries
import interopSample.mappers.toDomain
import interopSample.models.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import utils.presentation.AsyncDispatcher

class InteropSampleDatabaseDataSource(
    private val weatherQueries: WeatherQueries
) {
    fun getWeatherFlow(): Flow<Weather> {
        return weatherQueries.selectAll()
            .asFlow()
            .mapToOne(AsyncDispatcher)
            .map { entity ->
                entity.toDomain()
            }
    }

    suspend fun getWeather(): Weather {
        return weatherQueries.selectAll { temperature, unit ->
            Weather(temperature = temperature, temperatureUnit = unit)
        }.awaitAsOne()
    }

    suspend fun saveWeather(weather: Weather) {
        weatherQueries.transaction {
            weatherQueries.deleteAll()
            weatherQueries.insertWeather(
                temperature = weather.temperature,
                unit = weather.temperatureUnit
            )
        }
    }
}
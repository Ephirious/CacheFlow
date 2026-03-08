package core.sqldelight

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import data.WeatherQueries
import org.cacheflow.db.Database
import org.koin.dsl.module

suspend fun createDriver(): SqlDriver {
    val schema = Database.Schema
    val driver = SqlDriverFactory().createDriver(schema, "app.db")
    schema.awaitCreate(driver)
    return driver
}

internal val sqlModule = module {
    // SqlDriver creating in initKoin!!

    single<Database> { Database(get()) }

    single<WeatherQueries> { get<Database>().weatherQueries }

}
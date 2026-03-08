package core.sqldelight

import app.cash.sqldelight.db.SqlDriver
import data.WeatherQueries
import org.cacheflow.db.Database
import org.koin.dsl.module


internal val sqlModule = module {
    single<SqlDriver> {
        SqlDriverFactory().createDriver(Database.Schema, name = "app.db")
    }

    single<Database> { Database(get()) }

    single<WeatherQueries> { get<Database>().weatherQueries }

}
package core.sqldelight

import app.cash.sqldelight.async.coroutines.awaitCreate
import data.WeatherEntity
import data.WeatherQueries
import org.cacheflow.db.Database
import org.koin.core.module.Module
import org.koin.dsl.module

suspend fun createDriver(isSW: Boolean): CustomSqlDriver {
    val schema = Database.Schema
    val driver = SqlDriverFactory().createDriver(schema, "app_db", isSW)
    schema.awaitCreate(driver)
    return driver
}

suspend fun getSqlDriverModule(isSW: Boolean): Module {
    val driver = createDriver(isSW)
    return module {
        single<CustomSqlDriver> {
            driver
        }
    }
}

internal val sqlModule = module {
    // SqlDriver creating in initKoin!!

    single<Database> {
        Database(
            driver = get<CustomSqlDriver>(),
            WeatherEntityAdapter = WeatherEntity.Adapter(
                temperatureAdapter = bigDecimalAdapter
            )
        )
    }

    single<WeatherQueries> { get<Database>().weatherQueries }

}
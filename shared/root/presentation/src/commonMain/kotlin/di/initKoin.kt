package di

import app.cash.sqldelight.db.SqlDriver
import core.coreModule
import core.sqldelight.createDriver
import interopSampleDataModule
import interopSamplePresentationModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


// do not use for JS code (see webMain)
suspend fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
): KoinApplication {
    val driver = createDriver()

    return startKoin {
        appDeclaration()

        modules(
            coreModule,
            interopSampleDataModule,
            interopSamplePresentationModule,
            module { // check sqlModule in core...
                single<SqlDriver> {
                    driver
                }
            }
        )
    }
}
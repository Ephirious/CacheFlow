package di

import core.coreModule
import core.sqldelight.getSqlDriverModule
import interopSampleDataModule
import interopSamplePresentationModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import settingsPresentationModule
import statsPresentationModule
import syncDataModule
import transactionsPresentationModule


// do not use for JS code (see webMain)
suspend fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
): KoinApplication {

    val (sqlDriverModule, _) = getSqlDriverModule(isSW = false)

    return startKoin {
        appDeclaration()

        modules(
            sqlDriverModule,
            coreModule,

            interopSampleDataModule,
            interopSamplePresentationModule,

            transactionsPresentationModule,

            statsPresentationModule,

            settingsPresentationModule,


            syncDataModule
        )
    }
}
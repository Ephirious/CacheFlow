package di

import core.coreModule
import core.sqldelight.getSqlDriverModule
import editorsDataModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import settingsDataModule
import settingsPresentationModule
import statsPresentationModule
import syncDataModule
import transactionsDataModule
import transactionsPresentationModule


// do not use for JS code (see webMain)
suspend fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
): KoinApplication {

    val sqlDriverModule = getSqlDriverModule(isSW = false)

    return startKoin {
        appDeclaration()

        modules(
            sqlDriverModule,
            coreModule,

            transactionsPresentationModule,
            transactionsDataModule,

            statsPresentationModule,

            settingsPresentationModule,
            settingsDataModule,

            editorsDataModule,

            syncDataModule
        )
    }
}
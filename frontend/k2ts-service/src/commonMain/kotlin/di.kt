import core.coreModule
import core.sqldelight.getSqlDriverModule
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import sync.repositories.SyncManagerSWImpl

suspend fun initKoinForWorker(): Koin {

    val existingKoin = GlobalContext.getOrNull()
    if (existingKoin != null) {
        return existingKoin
    }

    val sqlDriverModule = getSqlDriverModule(isSW = true)

    return startKoin {
        modules(
            sqlDriverModule,
            coreModule,
            syncDataModule { remoteDataSource ->
                SyncManagerSWImpl(remoteDataSource)
            },

            interopSampleDataModule
        )
    }.koin
}
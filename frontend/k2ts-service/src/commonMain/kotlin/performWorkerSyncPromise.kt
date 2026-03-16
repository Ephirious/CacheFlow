import core.coreModule
import core.sqldelight.CustomSqlDriver
import core.sqldelight.getSqlDriverModule
import interopSample.usecases.RefreshWeatherUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import sync.repositories.SyncManager
import sync.repositories.SyncManagerSWImpl

@OptIn(ExperimentalJsExport::class)
fun performWorkerSyncPromise() = CoroutineScope(Dispatchers.Default).promise {
    println("[INFO-ServiceWorker] Perform")
    val koin = initKoinForWorker()

    koin.get<CustomSqlDriver>().reloadDb()
    koin.get<SyncManager>().requestSync()
    koin.get<RefreshWeatherUseCase>().invoke()
    println("[INFO-ServiceWorker] Perform done")
}

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

import core.sqldelight.CustomSqlDriver
import interopSample.usecases.RefreshWeatherUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import sync.repositories.SyncManager

@OptIn(ExperimentalJsExport::class)
fun serviceSyncPromise() = CoroutineScope(Dispatchers.Default).promise {
    println("[INFO-ServiceWorker] ServiceSync started!")
    val koin = initKoinForWorker()

    koin.get<CustomSqlDriver>().reloadDb()
    koin.get<SyncManager>().requestSync()
    koin.get<RefreshWeatherUseCase>().invoke()
    println("[INFO-ServiceWorker] ServiceSync done!")
}

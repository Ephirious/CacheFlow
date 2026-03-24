import core.sqldelight.CustomSqlDriver
import interopSample.usecases.RefreshWeatherUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import utils.Logg

@OptIn(ExperimentalJsExport::class)
fun serviceSyncPromise() = CoroutineScope(Dispatchers.Default).promise {
    Logg.debug { "SyncPromise started" }
    val koin = initKoinForWorker()

    koin.get<CustomSqlDriver>().reloadDb()
//    koin.get<SyncManager>().forceSync()
    koin.get<RefreshWeatherUseCase>().invoke()
    Logg.debug { "SyncPromise done!" }
}

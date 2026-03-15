import app.cash.sqldelight.db.SqlDriver
import core.coreModule
import core.sqldelight.SqlJsDriverSW
import core.sqldelight.getSqlDriverModule
import interopSample.usecases.GetWeatherUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import sync.repositories.SyncManager

@OptIn(ExperimentalJsExport::class)
fun performWorkerSyncPromise() = CoroutineScope(Dispatchers.Default).promise {
    println("Performing worker sync!!")
    val koin = initKoinForWorker()
    koin.get<SyncManager>().requestSync()


    (koin.get<SqlDriver>() as SqlJsDriverSW).reloadDbFromDisk()
    println(koin.get<GetWeatherUseCase>()())
    println("Done")
}

suspend fun initKoinForWorker(): Koin {

    val existingKoin = GlobalContext.getOrNull()
    if (existingKoin != null) {
        return existingKoin
    }

    val (sqlDriverModule, _) = getSqlDriverModule(isSW = true)

    return startKoin {

        modules(
            sqlDriverModule,
            coreModule,
            syncDataModule,
            interopSampleDataModule
        )
    }.koin
}

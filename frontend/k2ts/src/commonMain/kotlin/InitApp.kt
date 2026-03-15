import di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import root.RootComponent
import startup.initRealRootComponent
import startup.observeNetwork
import startup.registerServiceWorker
import sync.repositories.SyncManager
import utils.presentation.AsyncDispatcher

@OptIn(ExperimentalJsExport::class)
@JsExport
fun initApp() = CoroutineScope(AsyncDispatcher).promise<RootComponent> {
    val koin = initKoin()

    val syncManager: SyncManager = koin.koin.get()

    registerServiceWorker()

    observeNetwork(
        syncManager = syncManager
    )

    syncManager.requestSync()


    val rootComponent = initRealRootComponent()

    return@promise rootComponent
}
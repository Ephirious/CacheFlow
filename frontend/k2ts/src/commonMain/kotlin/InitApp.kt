import di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import root.RootComponent
import startup.initRealRootComponent
import startup.observeNetwork
import startup.sw.registerPeriodicSync
import startup.sw.registerServiceWorker
import sync.repositories.SyncManager
import utils.Logg
import utils.presentation.AsyncDispatcher

@OptIn(ExperimentalJsExport::class)
@JsExport
fun initApp() = CoroutineScope(AsyncDispatcher).promise<RootComponent> {
    Logg.setup("App")
    registerServiceWorker()
    registerPeriodicSync()
//    setupPushNotifications() TODO

    val koin = initKoin()

    val syncManager: SyncManager = koin.koin.get()

    observeNetwork(
        syncManager = syncManager
    )

    syncManager.requestSync()

    val rootComponent = initRealRootComponent()

    return@promise rootComponent
}
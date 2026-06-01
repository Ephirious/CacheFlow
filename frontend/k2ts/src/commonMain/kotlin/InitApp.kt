import data.SyncInternalQueries
import di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import root.RootComponent
import settings.usecases.theme.GetThemeUseCase
import startup.initRealRootComponent
import startup.observeNetwork
import startup.registerServiceWorker
import startup.setJsTheme
import sync.repositories.SyncManager
import utils.Logg
import utils.presentation.AsyncDispatcher

@OptIn(ExperimentalJsExport::class)
@JsExport
fun initApp() = CoroutineScope(AsyncDispatcher).promise<RootComponent> {
    Logg.setup("App")
    registerServiceWorker()
//    setupPushNotifications() TODO

    val koin = initKoin()
    setJsTheme(koin.koin.get<GetThemeUseCase>()())
    val syncManager: SyncManager = koin.koin.get()

    koin.koin.get<SyncInternalQueries>().initSettings()

    observeNetwork(
        syncManager = syncManager
    )

    syncManager.scope.launch {
        syncManager.forceSync(false)
    }

    val rootComponent = initRealRootComponent()

    return@promise rootComponent
}
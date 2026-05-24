import data.SyncInternalQueries
import di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import org.koin.dsl.module
import root.RootComponent
import settings.usecases.theme.GetThemeUseCase
import startup.initRealRootComponent
import startup.observeNetwork
import startup.registerServiceWorker
import startup.setJsTheme
import sync.repositories.SyncManager
import utils.Logg
import utils.NetworkObserver
import utils.presentation.AsyncDispatcher

@OptIn(ExperimentalJsExport::class)
@JsExport
fun initApp() = CoroutineScope(AsyncDispatcher).promise<RootComponent> {
    Logg.setup("App")
    registerServiceWorker()
//    setupPushNotifications() TODO

    val koin = initKoin(
        appDeclaration = {
            modules(
                module {
                    single<NetworkObserver> { JsNetworkObserver() }
                }
            )
        }
    )
    setJsTheme(koin.koin.get<GetThemeUseCase>()())
    val syncManager: SyncManager = koin.koin.get()

    koin.koin.get<SyncInternalQueries>().initSettings()

    observeNetwork(
        syncManager = syncManager
    )

    syncManager.requestSync()

    val rootComponent = initRealRootComponent()

    return@promise rootComponent
}
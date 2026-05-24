import kotlinx.browser.window
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.events.Event
import utils.NetworkObserver
import utils.NetworkStatus

class JsNetworkObserver : NetworkObserver {

    override val isOnline: Boolean
        get() = window.navigator.onLine

    override val status: Flow<NetworkStatus> = callbackFlow {
        val onlineListener: (Event) -> Unit = { trySend(NetworkStatus.Online) }
        val offlineListener: (Event) -> Unit = { trySend(NetworkStatus.Offline) }

        window.addEventListener("online", onlineListener)
        window.addEventListener("offline", offlineListener)

        trySend(if (isOnline) NetworkStatus.Online else NetworkStatus.Offline)

        awaitClose {
            window.removeEventListener("online", onlineListener)
            window.removeEventListener("offline", offlineListener)
        }
    }
}
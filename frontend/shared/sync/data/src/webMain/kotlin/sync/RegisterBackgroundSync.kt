package sync

import kotlinx.coroutines.await
import org.w3c.workers.ServiceWorkerRegistration
import utils.getServiceContainer
import kotlin.js.Promise


const val SYNC_TAG = "sync-send"
const val PERIODIC_SYNC_TAG = "periodic-sync-send"

external interface BackgroundSyncManager {
    fun register(tag: String): Promise<Unit>
    fun getTags(): Promise<Array<String>>
}

val ServiceWorkerRegistration.backgroundSyncManager
    get() = asDynamic().sync.unsafeCast<BackgroundSyncManager?>()

actual suspend fun registerBackgroundSync() {

    val registration = getServiceContainer()?.ready?.await()
    if (registration != null) {
        val syncManager = registration.backgroundSyncManager ?: return

        val tags = syncManager.getTags().await()

        if (!tags.contains(SYNC_TAG)) {
            syncManager.register(SYNC_TAG).await()
            println("[INFO-App] Registered new background sync")
        }
        println("[INFO-App] Background sync already registered")
    } else {
        println("[INFO-App] Can't register background sync: there is no serviceWorker")
    }
}
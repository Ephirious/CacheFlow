package startup.sw

import kotlinx.coroutines.await
import org.w3c.workers.ServiceWorkerRegistration
import sync.PERIODIC_SYNC_TAG
import utils.getServiceContainer
import kotlin.js.Promise

external interface PeriodicSyncManager {
    fun register(tag: String, options: dynamic): Promise<Unit>
    fun getTags(): Promise<Array<String>>
}

val ServiceWorkerRegistration.periodicSyncManager
    get() = asDynamic().periodicSync.unsafeCast<PeriodicSyncManager?>()

suspend fun registerPeriodicSync() {
    val registration = getServiceContainer()?.ready?.await()
    if (registration != null) {
        val manager = registration.periodicSyncManager ?: return

        val tags = manager.getTags().await()
        if (!tags.contains(PERIODIC_SYNC_TAG)) {
            val options = js("{}")
            options["minInterval"] = 12 * 60 * 60 * 1000 // 12 hours (по идее)

            try {
                manager.register("periodic-sync-db", options).await()
                println("[INFO-App] Periodic Sync registered")
            } catch (_: Throwable) {
                println("[WARN-App] Periodic Sync registration failed (Needs PWA installed & high engagement)")
            }
        }
    } else {
        println("[WARN-App] Can't register periodic sync – there is no serviceWorker")
    }
}
package sync

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.workers.ServiceWorkerRegistration
import kotlin.js.Promise


external interface BackgroundSyncManager {
    fun register(tag: String): Promise<Unit>
    fun getTags(): Promise<Array<String>>
}

val ServiceWorkerRegistration.backgroundSyncManager
    get() = asDynamic().sync.unsafeCast<BackgroundSyncManager?>()

actual suspend fun registerBackgroundSync() {

    val registration =
        window.navigator.serviceWorker
            .ready
            .await()

    val syncManager = registration.backgroundSyncManager ?: return

    val tags = syncManager.getTags().await()

    if (!tags.contains("sync-transactions")) {
        syncManager.register("sync-transactions").await()
        println("registered")
    }
    println("already registered")
}
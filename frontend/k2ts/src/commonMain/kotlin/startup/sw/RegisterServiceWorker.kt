package startup.sw

import kotlinx.coroutines.await
import org.w3c.dom.MODULE
import org.w3c.dom.WorkerType
import org.w3c.workers.RegistrationOptions
import utils.Logg
import utils.getServiceContainer

suspend fun registerServiceWorker() {
    val container = getServiceContainer()

    container
        ?.register(
            "/src/workers/sw-loader.js",
            options = RegistrationOptions(type = WorkerType.MODULE, scope = "/")
        )
        ?.await()

    container?.ready?.await()

    if (container != null) {
        Logg.debug { "Registered ServiceWorker" }
    } else {
        Logg.error { "There is no ServiceWorker – can't register it" }
    }
}
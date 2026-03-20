package startup.sw

import kotlinx.coroutines.await
import org.w3c.dom.CLASSIC
import org.w3c.dom.WorkerType
import org.w3c.workers.RegistrationOptions
import utils.getServiceContainer

suspend fun registerServiceWorker() {
    val container = getServiceContainer()

    container
        ?.register(
            "/sw-loader.js",
            options = RegistrationOptions(type = WorkerType.CLASSIC, scope = "/")
        )
        ?.await()

    container?.ready?.await()

    if (container != null) {
        println("[INFO-App] Registered service")
    } else {
        println("[INFO-App] No service")
    }
}
package startup

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.dom.CLASSIC
import org.w3c.dom.WorkerType
import org.w3c.workers.RegistrationOptions

suspend fun registerServiceWorker() {
    val container = window.navigator.serviceWorker


    container
        .register(
            "/sw-loader.js",
            options = RegistrationOptions(type = WorkerType.CLASSIC, scope = "/")
        )
        .await()

    container.ready.await()
    println("[INFO-ServiceWorker] Registered service")
}
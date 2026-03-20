package utils

import kotlinx.browser.window
import org.w3c.workers.ServiceWorkerContainer

fun getServiceContainer(): ServiceWorkerContainer? {
    val serviceContainer = window.navigator.serviceWorker
    if (serviceContainer.asDynamic() != undefined) {
        return serviceContainer
    }
    return null
}
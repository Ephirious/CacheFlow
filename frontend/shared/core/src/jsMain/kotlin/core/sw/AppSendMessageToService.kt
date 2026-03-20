package core.sw

import kotlinx.browser.window

fun appSendMessageToService(message: String) {
    val controller = window.navigator.serviceWorker.controller

    if (controller != null) {
        controller.postMessage(message)
        println("[INFO-ServiceWorker] Sent: $message")
    } else {
        println("[INFO-ServiceWorker] Can't send message: there is no service")
    }
}
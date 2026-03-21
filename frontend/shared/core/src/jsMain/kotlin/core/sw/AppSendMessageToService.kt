package core.sw

import kotlinx.browser.window
import utils.Logg

fun appSendMessageToService(message: String) {
    val controller = window.navigator.serviceWorker.controller

    if (controller != null) {
        controller.postMessage(message)
        Logg.debug { "Sent: $message" }
    } else {
        Logg.error { "Can't send message: there is no service" }
    }
}
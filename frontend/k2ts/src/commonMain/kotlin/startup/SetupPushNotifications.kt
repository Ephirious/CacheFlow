package startup

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set
import org.w3c.notifications.DEFAULT
import org.w3c.notifications.GRANTED
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationPermission
import utils.AppConfig.pushVapidPublicKey
import utils.AppConfig.serverIP
import utils.AppConfig.serverPort
import utils.AppConfig.urlSchemeString
import utils.Logg
import utils.getServiceContainer
import kotlin.js.json

suspend fun setupPushNotifications() {
    try {
        val permission =
            if (Notification.permission == NotificationPermission.DEFAULT)
                Notification.requestPermission().await()
            else
                Notification.permission
        if (permission == NotificationPermission.GRANTED) {
            val registration = getServiceContainer()?.ready?.await()
            subscribeToPush(registration).then { subscription ->
                if (subscription != null) {
                    sendSubscriptionToServer(subscription)
                }
            }
        }
    } catch (e: Exception) {
        Logg.error { "Push setup failed: ${e.message}" }
    }
}

private fun subscribeToPush(reg: dynamic): dynamic {
    return reg.pushManager.getSubscription().then { existing ->
        if (existing != null) {
            return@then existing
        }

        val appKey = urlBase64ToUint8Array(pushVapidPublicKey)

        return@then reg.pushManager.subscribe(
            json(
                "userVisibleOnly" to true,
                "applicationServerKey" to appKey,
            )
        )
    }
}


private fun urlBase64ToUint8Array(base64String: String): Uint8Array {
    val padding = "=".repeat((4 - base64String.length % 4) % 4)
    val base64 = (base64String + padding)
        .replace("-", "+")
        .replace("_", "/")

    val rawData = window.atob(base64)
    val output = Uint8Array(rawData.length)

    for (i in 0 until rawData.length) {
        output[i] = rawData[i].code.toByte()
    }
    return output
}

private fun sendSubscriptionToServer(
    @Suppress("unused")
    subscription: dynamic
) {
    window.fetch(
        "$urlSchemeString$serverIP:$serverPort/subscribe",
        js(
            """{
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(subscription)
        }"""
        )
    )
}
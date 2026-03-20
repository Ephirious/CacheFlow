package startup

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.notifications.DEFAULT
import org.w3c.notifications.GRANTED
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationPermission
import utils.AppConfig.pushVapidPublicKey
import utils.AppConfig.serverIP
import utils.AppConfig.serverPort
import utils.AppConfig.urlSchemeString
import utils.getServiceContainer

suspend fun setupPushNotifications() {
    try {
        val permission =
            if (Notification.permission == NotificationPermission.DEFAULT)
                Notification.requestPermission().await()
            else
                Notification.permission
        if (permission == NotificationPermission.GRANTED) {
            val registration = getServiceContainer()?.ready?.await()
            val subscription = subscribeToPush(registration)
            sendSubscriptionToServer(subscription)
        }
    } catch (e: Exception) {
        println("[ERROR-App] Push setup failed: ${e.message}")
    }
}

fun subscribeToPush(reg: dynamic): dynamic {
    return reg.pushManager.getSubscription().then { existing ->
        if (existing != null) {
            return@then existing
        }

        val appKey = urlBase64ToUint8Array(pushVapidPublicKey)

        return@then reg.pushManager.subscribe(
            js(
                """{
        userVisibleOnly: true,
        applicationServerKey: appKey
    }"""
            )
        )
    }
}


fun urlBase64ToUint8Array(base64String: String): dynamic {
    val padding = "=".repeat((4 - base64String.length % 4) % 4)
    val base64 = (base64String + padding).replace("-", "+").replace("_", "/")
    val rawData = window.asDynamic().atob(base64) as String

    val output = js("new Uint8Array(rawData.length)")
    for (i in 0 until rawData.length) {
        output[i] = rawData.asDynamic().charCodeAt(i)
    }
    return output
}

suspend fun sendSubscriptionToServer(
    @Suppress("unused")
    subscription: dynamic
) {
    window.fetch(
        "$urlSchemeString$serverIP:$serverPort/subscribe", js(
            """{
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(subscription)
    }"""
        )
    ).await()
}
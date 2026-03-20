import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import org.w3c.dom.events.Event

external class PushEvent : Event {
    val data: PushMessageData
    fun waitUntil(f: dynamic)
}

external interface PushMessageData {
    fun text(): String
    fun json(): dynamic
}

@OptIn(ExperimentalJsExport::class)
fun servicePushPromise(pushEvent: PushEvent) = CoroutineScope(Dispatchers.Default).promise {
    val data = try {
        pushEvent.data.text()
    } catch (e: Throwable) {
        "Новое уведомление"
    }

    val options = js(
        """{
            body: data,
            icon: '/icon.png',
            badge: '/badge.png',
            tag: 'weekly-reminder'+Date.now()
        }"""
    )
    println("[INFO-ServiceWorker] Try to push: $data")
    self.registration.showNotification("CacheFlow", options)
}

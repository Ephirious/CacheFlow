import sync.SYNC_TAG

external val self: dynamic

fun main() {
    self.addEventListener("sync") { event ->
        if (event.tag == SYNC_TAG) {
            event.waitUntil(serviceSyncPromise())
        }
    }

    self.addEventListener("push") { event: PushEvent ->
        event.waitUntil(servicePushPromise(event))
    }

    self.addEventListener("notificationclick") { event ->
        event.notification.close()
        event.waitUntil(
            self.clients.openWindow("/")
        )
    }
}
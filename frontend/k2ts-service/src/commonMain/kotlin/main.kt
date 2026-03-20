import sync.PERIODIC_SYNC_TAG
import sync.SYNC_TAG
import utils.Logg

external val self: dynamic

fun main() {
    Logg.setup("SW")
    self.addEventListener("sync") { event ->
        if (event.tag == SYNC_TAG) {
            event.waitUntil(serviceSyncPromise())
        }
    }

    self.addEventListener("periodicsync") { event ->
        if (event.tag == PERIODIC_SYNC_TAG) {
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
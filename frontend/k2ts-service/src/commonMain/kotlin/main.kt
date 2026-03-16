import sync.SYNC_TAG

external val self: dynamic

fun main() {
    self.addEventListener("sync") { event: dynamic ->
        if (event.tag == SYNC_TAG) {
            event.waitUntil(performWorkerSyncPromise())
        }
    }
}
external val self: dynamic

fun main() {
    self.addEventListener("sync") { event: dynamic ->
        if (event.tag == "sync-transactions") {
            event.waitUntil(performWorkerSyncPromise())
        }
    }
}
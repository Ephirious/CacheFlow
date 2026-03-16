package sync.repositories

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json.Default.encodeToString
import sync.cloud.SyncRemoteDataSource
import utils.data.withWebLock

external val self: dynamic

class SyncManagerSWImpl(
    remoteDataSource: SyncRemoteDataSource
) : SyncManagerImplABC(
    remoteDataSource = remoteDataSource
) {

    private val mutex = Mutex()

    private fun setStatus(newStatus: SyncStatus) {

        val encodedStatus = encodeToString(
            AppServiceMessage.serializer(),
            AppServiceMessage.StatusChanged(newStatus)
        )
        self.clients.matchAll(js("{ type: 'window', includeUncontrolled: true }"))
            .then { clients ->
                for (client in clients) {
                    client.postMessage(encodedStatus)
                    println("[INFO-ServiceWorker] Sent: $encodedStatus")
                }
            }

        status.value = newStatus
    }

    override suspend fun requestSync() {
        mutex.withLock {
            withWebLock(scope) {
                syncWithStatusCallback { newStatus ->
                    setStatus(newStatus)
                }
            }
        }
    }
}
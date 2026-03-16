package sync.repositories

import core.sw.swSendMessageToClients
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json.Default.encodeToString
import sync.cloud.SyncRemoteDataSource
import utils.data.withWebLock


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
        swSendMessageToClients(encodedStatus)

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
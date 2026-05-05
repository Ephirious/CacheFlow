package sync.repositories

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import data.GetSyncQueue
import data.SyncDBQueries
import data.SyncInternalQueries
import kotlinx.coroutines.flow.Flow
import utils.presentation.AsyncDispatcher

class SyncQueueRepositoryImpl(
    private val syncQueries: SyncDBQueries,
    private val syncInternalQueries: SyncInternalQueries
) : SyncQueueRepository {

    override suspend fun <T> withSyncDisabled(block: suspend () -> T): T {
        syncInternalQueries.updateSyncStatus(true)

        return try {
            block()
        } finally {
            syncInternalQueries.updateSyncStatus(false)
        }
    }

    override suspend fun getUnsynced(): List<GetSyncQueue> =
        syncQueries.getSyncQueue().awaitAsList()

    override suspend fun deleteFromQueue(ids: List<String>) {
        syncQueries.transaction {
            ids.forEach { id ->
                syncQueries.deleteSyncOperations(id)
            }
        }
    }

    override suspend fun deleteByProcessingIds(pIds: List<String>) {
        syncQueries.deleteByProcessingIds(pIds)
    }

    override fun observeQueueSize(): Flow<Long> {
        return syncQueries.countAll().asFlow()
            .mapToOne(AsyncDispatcher)
    }

    override suspend fun clearAll() {
        syncQueries.clearAllSyncOperationss()
    }
}
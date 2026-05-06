package sync.repositories

import data.GetSyncQueue
import kotlinx.coroutines.flow.Flow


interface SyncQueueRepository {
    fun getUnsyncedFlow(): Flow<List<GetSyncQueue>>

    suspend fun getUnsynced(): List<GetSyncQueue>

    suspend fun deleteFromQueue(ids: List<String>)

    fun observeQueueSize(): Flow<Long>

    suspend fun clearAll()

    suspend fun <T> withSyncDisabled(block: suspend () -> T): T

    suspend fun deleteByProcessingIds(pIds: List<String>)
}
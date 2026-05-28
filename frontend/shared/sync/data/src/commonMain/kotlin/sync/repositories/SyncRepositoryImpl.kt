package sync.repositories

import data.SyncInternalQueries
import sync.local.SyncLocalDataSource

class SyncRepositoryImpl(
    private val localDataSource: SyncLocalDataSource,
    private val syncInternalQueries: SyncInternalQueries
) : SyncRepository {
    override fun resetLastSyncDate() {
        localDataSource.setLastTimeSync(SyncLocalDataSource.DEFAULT_SYNC_TIMESTAMP)
    }

    override suspend fun setSyncLock(isSyncRunning: Boolean) {
        syncInternalQueries.updateSyncStatus(isSyncRunning)
    }
}
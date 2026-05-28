package sync.repositories

import sync.local.SyncLocalDataSource

class SyncRepositoryImpl(
    private val localDataSource: SyncLocalDataSource,
) : SyncRepository {
    override fun resetLastSyncDate() {
        localDataSource.setLastTimeSync(SyncLocalDataSource.DEFAULT_SYNC_TIMESTAMP)
    }
}
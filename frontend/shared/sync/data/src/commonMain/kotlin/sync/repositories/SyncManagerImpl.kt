package sync.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import sync.cloud.SyncRemoteDataSource

class SyncManagerImpl(
    private val remoteDataSource: SyncRemoteDataSource
) : SyncManager {

    override val status: StateFlow<SyncStatus> = MutableStateFlow(SyncStatus.Ok)


    override suspend fun sync() {
        // do sync
        // if failed -> change status
    }

    override suspend fun observeDb() {
        // do observe Db
    }
}

//class SyncManagerJSImpl(
//    private val syncManager: SyncManager
//) : SyncManagerJS {
//    override fun sync() = syncManager.coroutineScope.promise {
//        syncManager.sync()
//    }
//
//    override val status by lazy {
//        syncManager.status.asJsValue(syncManager.coroutineScope)
//    }
//}
package sync.repositories

import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json.Default.decodeFromString
import sync.cloud.SyncRemoteDataSource
import sync.registerBackgroundSync
import utils.data.withWebLock

class SyncManagerAppImpl(
    remoteDataSource: SyncRemoteDataSource,
) : SyncManagerImplABC(
    remoteDataSource = remoteDataSource
) {
    override suspend fun requestSync() {
        scheduler.schedule()
    }

    private val scheduler =
        SyncScheduler(scope, listOf(getTransactionsFlowUseCase(), getCategoriesFlowUseCase(), getAccountsFlowUseCase()))


    private val mutex = Mutex()

    init {
        scope.launch {
            scheduler.debouncedSyncEvents.collect {
                trySync()
            }
        }
        window.navigator.serviceWorker.onmessage = { message ->
            (message.data as? String)?.let { data ->
                println("[INFO-App] Catch from service $data")
                when (val msg = decodeFromString<AppServiceMessage>(data)) {
                    AppServiceMessage.DataUpdated -> TODO()
                    is AppServiceMessage.StatusChanged -> {
                        msg.status
                    }
                }
            }

        }
    }

    private suspend fun trySync() {
        mutex.withLock {
            withWebLock(scope) {
                syncWithStatusCallback { newStatus ->
                    status.value = newStatus
                    if (newStatus == SyncStatus.Failed) {
                        registerBackgroundSync()
                    }
                }
            }
        }
    }
}
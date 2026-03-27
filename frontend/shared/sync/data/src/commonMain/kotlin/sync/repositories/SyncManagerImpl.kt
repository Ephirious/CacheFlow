package sync.repositories

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import sync.cloud.SyncRemoteDataSource
import utils.Logg
import utils.data.withWebLock
import utils.presentation.AsyncDispatcher

class SyncManagerImpl(
    override val scope: CoroutineScope = CoroutineScope(AsyncDispatcher + SupervisorJob()),
    remoteDataSource: SyncRemoteDataSource,
) : SyncManager, KoinComponent {

    override val status = MutableStateFlow(SyncStatus.Ok)

    private val mutex = Mutex()


    private val scheduler =
        SyncScheduler(scope, listOf(/*todo*/))

    init {
        scope.launch(AsyncDispatcher) {
            scheduler.debouncedSyncEvents.collect {
                trySync()
            }
        }
    }

    override suspend fun requestSync() {
        scheduler.schedule()
    }

    override suspend fun forceSync() = trySync()


    private suspend fun trySync() {
        mutex.withLock {
            withWebLock(scope) {

                runCatching {
                    status.value = SyncStatus.InProcess
                    sync()
                }.fold(
                    onSuccess = {
                        status.value = SyncStatus.Ok
                    },
                    onFailure = {
                        status.value = SyncStatus.Failed
                    }
                )
            }
        }
    }

    private suspend fun sync() {
        Logg.debug { "Syncing start" }
        // TODO
        get<HttpClient>().get(urlString = "http://localhost:8000/sync")
        Logg.debug { "Syncing end" }
//        val allTrans = getTransactionsFlowUseCase().firstOrNull() ?: listOf()
//        val allCats = getCategoriesFlowUseCase().firstOrNull() ?: listOf()
//        val allAccounts = getAccountsFlowUseCase().firstOrNull() ?: listOf()
//
//        val unsyncedData = SyncData(
//            transactions = filterUnsynced(allTrans),
//            categories = filterUnsynced(allCats),
//            accounts = filterUnsynced(allAccounts)
//        )


//        if (unsyncedData.isEmpty()) {
//            return
//        }
//
//        remoteDataSource.sync(unsyncedData)
    }
}

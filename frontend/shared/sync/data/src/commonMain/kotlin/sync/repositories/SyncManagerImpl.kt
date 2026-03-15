package sync.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import sync.cloud.SyncRemoteDataSource
import sync.registerBackgroundSync
import utils.presentation.AsyncDispatcher


@Serializable
data class SyncData(
    val transactions: List<SampleDBData>,
    val categories: List<SampleDBData>,
    val accounts: List<SampleDBData>,
)

@Serializable
data class SampleDBData(
    val timestamp: String,
    val isSynced: Boolean
)

fun createMockFlow(name: String): Flow<List<SampleDBData>> = flow {
//    while (true) {
//        val delaySec = Random.nextLong(5, 20)
//        delay(delaySec * 1000)
//
//        println("[Mock] 🕒 Поток '$name' сгенерировал обновление ($delaySec sec)")
//        emit(listOf(SampleDBData("timestamp-${Clock.System.now().epochSeconds}", false)))
//    }
}

class SyncManagerImpl(
    private val remoteDataSource: SyncRemoteDataSource,
    scope: CoroutineScope = CoroutineScope(AsyncDispatcher),
    private val getTransactionsFlowUseCase: () -> Flow<List<SampleDBData>> = { createMockFlow("Transactions") },
    private val getCategoriesFlowUseCase: () -> Flow<List<SampleDBData>> = { createMockFlow("Categories") },
    private val getAccountsFlowUseCase: () -> Flow<List<SampleDBData>> = { createMockFlow("Accounts") }
) : SyncManager {


    override val status = MutableStateFlow(SyncStatus.Ok)


    private val scheduler =
        SyncScheduler(scope, listOf(getTransactionsFlowUseCase(), getCategoriesFlowUseCase(), getAccountsFlowUseCase()))

    private val mutex = Mutex()

    init {
        scope.launch {
            scheduler.debouncedSyncEvents.collect {
                trySync()
            }
        }
    }

    private fun filterUnsynced(data: List<SampleDBData>) = data.filter { !it.isSynced }


    override fun requestSync() {
        scheduler.schedule()
    }

    private suspend fun trySync() {
        mutex.withLock {
            runCatching {
                status.value = SyncStatus.InProcess
                throw Exception()
//                sync()
            }.fold(
                onSuccess = {
                    status.value = SyncStatus.Ok
                },
                onFailure = {
                    println("Try to register")
                    status.value = SyncStatus.Failed
                    registerBackgroundSync()
                }
            )
        }
    }

    private suspend fun sync() {
        val allTrans = getTransactionsFlowUseCase().first()
        val allCats = getCategoriesFlowUseCase().first()
        val allAccounts = getAccountsFlowUseCase().first()

        val unsyncedData = SyncData(
            transactions = filterUnsynced(allTrans),
            categories = filterUnsynced(allCats),
            accounts = filterUnsynced(allAccounts)
        )


//        if (unsyncedData.isEmpty()) {
//            return
//        }
//
//        remoteDataSource.sync(unsyncedData)
    }

}
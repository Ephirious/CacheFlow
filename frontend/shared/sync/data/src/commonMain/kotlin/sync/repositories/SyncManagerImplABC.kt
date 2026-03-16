package sync.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import sync.cloud.SyncRemoteDataSource
import utils.presentation.AsyncDispatcher

abstract class SyncManagerImplABC(
    protected val remoteDataSource: SyncRemoteDataSource,
    override val scope: CoroutineScope = CoroutineScope(AsyncDispatcher + SupervisorJob())
) : SyncManager {
    override val status = MutableStateFlow(SyncStatus.Ok)

    protected val getTransactionsFlowUseCase: () -> Flow<List<SampleDBData>> = { createMockFlow("Transactions") }
    protected val getCategoriesFlowUseCase: () -> Flow<List<SampleDBData>> = { createMockFlow("Categories") }
    protected val getAccountsFlowUseCase: () -> Flow<List<SampleDBData>> = { createMockFlow("Accounts") }

    protected suspend fun syncWithStatusCallback(
        onStatusChange: suspend (SyncStatus) -> Unit
    ) {
        runCatching {
            onStatusChange(SyncStatus.InProcess)
            sync()
        }.fold(
            onSuccess = {
                onStatusChange(SyncStatus.Ok)
            },
            onFailure = {
                onStatusChange(SyncStatus.Failed)
            }
        )
    }

    private suspend fun sync() {
        println("TODO SYNC METHOD")
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
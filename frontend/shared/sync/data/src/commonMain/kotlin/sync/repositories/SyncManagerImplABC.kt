package sync.repositories

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import sync.cloud.SyncRemoteDataSource
import utils.Logg
import utils.presentation.AsyncDispatcher

abstract class SyncManagerImplABC(
    protected val remoteDataSource: SyncRemoteDataSource,
    override val scope: CoroutineScope = CoroutineScope(AsyncDispatcher + SupervisorJob())
) : SyncManager, KoinComponent {
    override val status = MutableStateFlow(SyncStatus.Ok)

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
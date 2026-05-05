package sync.repositories

import editors.repositories.AccountsRepository
import editors.repositories.CategoriesRepository
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import sync.cloud.SyncRemoteDataSource
import sync.cloud.dtos.SyncRequest
import sync.cloud.dtos.SyncTableType
import sync.mappers.mapSyncQueueRow
import utils.Logg
import utils.data.withWebLock
import utils.presentation.AsyncDispatcher

class SyncManagerImpl(
    override val scope: CoroutineScope = CoroutineScope(AsyncDispatcher + SupervisorJob()),
    private val remoteDataSource: SyncRemoteDataSource,
    private val queueRepo: SyncQueueRepository,
    private val accountsRepo: AccountsRepository,
    private val categoriesRepo: CategoriesRepository
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
        val unsyncedRows = queueRepo.getUnsynced()
        val dtoOperations = unsyncedRows.map { mapSyncQueueRow(it) }
        val request = SyncRequest(operations = dtoOperations, lastSyncDate = TODO())
        val response = remoteDataSource.sendSyncRequest(request)

        queueRepo.withSyncDisabled {
            if (response.acceptedIds.isNotEmpty()) {
                queueRepo.deleteByProcessingIds(response.acceptedIds)
            }
            response.deleteOperations.forEach {
               op ->
                when(op.tableType) {
                    SyncTableType.ACCOUNTS -> accountsRepo.softDeleteAccount(op.id)
                    SyncTableType.CATEGORIES -> categoriesRepo.softDeleteCategory(op.id)
                    else -> TODO()
                }
            }

            response.updateState.forEach {
                upd ->
                when(upd.tableType) {
                    SyncTableType.ACCOUNTS -> {
                        val r = upd.getTypedRecord(TODO("DI FOR SERIALIZATION"))
                    }
                    else -> TODO()
                }
            }
        }

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

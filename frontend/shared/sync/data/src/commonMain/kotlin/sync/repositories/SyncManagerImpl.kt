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
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import sync.cloud.SyncRemoteDataSource
import sync.cloud.dtos.AccountOutDTO
import sync.cloud.dtos.CategoryRecordCreateDTO
import sync.cloud.dtos.SyncRequest
import sync.cloud.dtos.SyncTableType
import sync.mappers.mapSyncQueueRow
import utils.Logg
import utils.data.withWebLock
import utils.presentation.AsyncDispatcher
import utils.types.HexColor

class SyncManagerImpl(
    override val scope: CoroutineScope = CoroutineScope(AsyncDispatcher + SupervisorJob()),
    private val remoteDataSource: SyncRemoteDataSource,
    private val queueRepo: SyncQueueRepository,
    private val accountsRepo: AccountsRepository,
    private val categoriesRepo: CategoriesRepository,
    private val json: Json,
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
                val record = upd.getTypedRecord(json)
                when(upd.tableType) {
                    SyncTableType.ACCOUNTS -> {
                        val accDto = record as AccountOutDTO
                        val color = HexColor(hex = accDto?.color.orEmpty())
                        accountsRepo.upsertAccount(id = accDto.id, name = accDto.name, color , stringAmount = accDto.funds)
                    }
                    SyncTableType.CATEGORIES -> {
                        val catDto = record as CategoryRecordCreateDTO
                        categoriesRepo.upsertCategory(id = catDto.id, name = catDto.name, emoji = catDto.emoji, type = catDto.type)
                    }
                    else -> TODO("ADD TRANSFERS AND OPERATIONS")
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

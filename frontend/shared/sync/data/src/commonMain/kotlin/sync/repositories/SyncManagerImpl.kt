package sync.repositories

import dbEnums.SyncTableType
import editors.repositories.AccountsRepository
import editors.repositories.CategoriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import sync.cloud.SyncRemoteDataSource
import sync.cloud.dtos.AccountOutDTO
import sync.cloud.dtos.CategoryRecordCreateDTO
import sync.cloud.dtos.OperationRecordCreateDTO
import sync.cloud.dtos.SyncRequest
import sync.cloud.dtos.TransferRecordCreateDTO
import sync.local.SyncLocalDataSource
import sync.mappers.mapSyncQueueRow
import transactions.repositories.TransactionsRepository
import utils.Logg
import utils.data.withWebLock
import utils.presentation.AsyncDispatcher
import utils.types.HexColor

class SyncManagerImpl(
    override val scope: CoroutineScope = CoroutineScope(AsyncDispatcher + SupervisorJob()),
    private val remoteDataSource: SyncRemoteDataSource,
    private val localDataSource: SyncLocalDataSource,
    private val queueRepo: SyncQueueRepository,
    private val accountsRepo: AccountsRepository,
    private val categoriesRepo: CategoriesRepository,
    private val transactionsRepo: TransactionsRepository
) : SyncManager, KoinComponent {

    override val status = MutableStateFlow(SyncStatus.Ok)

    private val mutex = Mutex()


    private val scheduler =
        SyncScheduler(scope, listOf(queueRepo.getUnsyncedFlow()))

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
                    onSuccess = { lastTimeSync ->
                        status.value = SyncStatus.Ok
                        localDataSource.setLastTimeSync(lastTimeSync)
                    },
                    onFailure = {
                        status.value = SyncStatus.Failed
                    }
                )
            }
        }
    }

    private suspend fun sync(): String {
        Logg.debug { "Syncing start" }

        val unsyncedRows = queueRepo.getUnsynced()
        if (!unsyncedRows.isEmpty()) {
            val dtoOperations = unsyncedRows.map { mapSyncQueueRow(it) }
            val request = SyncRequest(operations = dtoOperations, lastSyncDate = localDataSource.getLastTimeSync())
            val response = remoteDataSource.sendSyncRequest(request)

            queueRepo.withSyncDisabled {
                if (response.acceptedIds.isNotEmpty()) {
                    queueRepo.deleteByProcessingIds(response.acceptedIds)
                }
                response.deleteOperations.forEach { op ->
                    when (op.tableType) {
                        SyncTableType.ACCOUNTS -> accountsRepo.softDeleteAccount(op.id)
                        SyncTableType.CATEGORIES -> categoriesRepo.softDeleteCategory(op.id)
                        SyncTableType.TRANSFER -> transactionsRepo.hardDeleteTransfer(op.id)
                        SyncTableType.OPERATIONS -> transactionsRepo.hardDeleteTransaction(op.id)
                    }
                }

                response.updateState.forEach { upd ->
                    val record = upd.getTypedRecord()
                    when (upd.tableType) {
                        SyncTableType.ACCOUNTS -> {
                            val accDto = record as AccountOutDTO
                            val color = HexColor(hex = accDto.color)
                            accountsRepo.upsertAccount(
                                id = accDto.id,
                                name = accDto.name,
                                color,
                                stringAmount = accDto.funds
                            )
                        }

                        SyncTableType.CATEGORIES -> {
                            val catDto = record as CategoryRecordCreateDTO
                            categoriesRepo.upsertCategory(
                                id = catDto.id,
                                name = catDto.name,
                                emoji = catDto.emoji,
                                type = catDto.type
                            )
                        }

                        SyncTableType.TRANSFER -> {
                            val transferDto = record as TransferRecordCreateDTO
                            transactionsRepo.badInsertTransfer(
                                id = transferDto.id,
                                accountFromId = transferDto.accountFromId,
                                accountToId = transferDto.accountToId
                            )
                        }

                        SyncTableType.OPERATIONS -> {
                            val operationDto = record as OperationRecordCreateDTO
                            transactionsRepo.badInsertTransaction(
                                id = operationDto.id,
                                accountUuid = operationDto.accountUuid,
                                transferId = operationDto.transferId,
                                categoryId = operationDto.categoryId,
                                amount = operationDto.amount,
                                date = operationDto.date,
                                notes = operationDto.notes
                            )
                        }
                    }

                }
            }
            Logg.debug { "Syncing end" }
            return response.lastSyncDate
        }

        Logg.debug { "Syncing wasn't started" }

        return localDataSource.getLastTimeSync()
    }
}

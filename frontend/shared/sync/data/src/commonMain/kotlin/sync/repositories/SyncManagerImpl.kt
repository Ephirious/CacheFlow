package sync.repositories

import auth.TokenStorage
import dbEnums.SyncTableType
import editors.repositories.AccountsRepository
import editors.repositories.CategoriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
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
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class SyncManagerImpl(
    override val scope: CoroutineScope = CoroutineScope(AsyncDispatcher + SupervisorJob()),
    private val remoteDataSource: SyncRemoteDataSource,
    private val localDataSource: SyncLocalDataSource,
    private val queueRepo: SyncQueueRepository,
    private val accountsRepo: AccountsRepository,
    private val categoriesRepo: CategoriesRepository,
    private val transactionsRepo: TransactionsRepository,


    private val tokenStorage: TokenStorage
) : SyncManager, KoinComponent {

    private val initialRetryDelay = 10.seconds
    private val maxRetryDelay = 5.minutes
    private var currentRetryDelay = initialRetryDelay

    override val status = MutableStateFlow(SyncStatus.Ok)

    private val mutex = Mutex()


    private val scheduler =
        SyncScheduler(scope, listOf(queueRepo.getUnsyncedFlow()))

    init {
        scope.launch(AsyncDispatcher) {
            scheduler.debouncedSyncEvents
                .onStart { Logg.debug { "DebounceSyncFlow started" } }
                .catch { e -> Logg.error { "DebounceSyncFlow error: ${e.message}" } }
                .collect {
                    trySync()
                }
        }
    }

    override suspend fun requestSync() {
        Logg.debug { "Syncing manual request" }
        scheduler.schedule()
    }

    override suspend fun forceSync(retry: Boolean) = trySync(retry = retry)


    private suspend fun trySync(retry: Boolean = true) {
        if (tokenStorage.isTokensEmpty()) {
            Logg.error { "CANT SYNC: No auth provided" }
            return
        }
        Logg.debug { "Syncing try start" }

        var result = SyncStatus.InProcess

        mutex.withLock {
            withWebLock(scope) {

                runCatching {
                    status.value = SyncStatus.InProcess
                    sync()
                }.fold(
                    onSuccess = { lastTimeSync ->
                        status.value = SyncStatus.Ok
                        localDataSource.setLastTimeSync(lastTimeSync)
                        currentRetryDelay = initialRetryDelay
                    },
                    onFailure = { error ->
                        status.value = SyncStatus.Failed
                        if (error is CancellationException) throw error

                        result = SyncStatus.Failed
                        Logg.error { "Syncing error: ${error.stackTraceToString()}" }
                    }
                )
            }
        }

        if (result == SyncStatus.Failed && retry) {
            val waitTime = currentRetryDelay

            currentRetryDelay = (currentRetryDelay * 2).coerceAtMost(maxRetryDelay)

            scope.launch {
                Logg.debug { "Will retry sync after $waitTime" }
                delay(waitTime)
                scheduler.schedule()
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
                        SyncTableType.accounts -> accountsRepo.softDeleteAccount(op.id)
                        SyncTableType.categories -> categoriesRepo.softDeleteCategory(op.id)
                        SyncTableType.transfer -> transactionsRepo.hardDeleteTransfer(op.id)
                        SyncTableType.operations -> transactionsRepo.hardDeleteTransaction(op.id)
                    }
                }

                response.updateState.forEach { upd ->
                    val record = upd.getTypedRecord()
                    when (upd.tableType) {
                        SyncTableType.accounts -> {
                            val accDto = record as AccountOutDTO
                            val color = HexColor(hex = accDto.color)
                            accountsRepo.upsertAccount(
                                id = accDto.id,
                                name = accDto.name,
                                color,
                                stringAmount = accDto.funds
                            )
                        }

                        SyncTableType.categories -> {
                            val catDto = record as CategoryRecordCreateDTO
                            categoriesRepo.upsertCategory(
                                id = catDto.id,
                                name = catDto.name,
                                emoji = catDto.emoji,
                                type = catDto.type
                            )
                        }

                        SyncTableType.transfer -> {
                            val transferDto = record as TransferRecordCreateDTO
                            transactionsRepo.badInsertTransfer(
                                id = transferDto.id,
                                accountFromId = transferDto.accountFromId,
                                accountToId = transferDto.accountToId
                            )
                        }

                        SyncTableType.operations -> {
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

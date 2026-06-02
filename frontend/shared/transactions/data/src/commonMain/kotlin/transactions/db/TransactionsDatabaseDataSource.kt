package transactions.db

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import transactions.mappers.listToDomain
import transactions.mappers.toDomain
import transactions.models.Transaction
import transactions.models.TransactionFilters
import transactions.models.TransactionType
import utils.bigDecimalExtensions.times
import utils.bigDecimalExtensions.unaryMinus
import utils.presentation.AsyncDispatcher
import utils.toInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class TransactionsDatabaseDataSource(
    val transactionsQueries: OperationsQueries,
    val transfersQueries: TransfersQueries,
    private val accountsQueries: AccountsQueries,
    private val commonQueries: CommonQueries
) {

    // не нашёл лучшего места...
    @OptIn(ExperimentalUuidApi::class)
    suspend fun initBase() {
        val newUuid7: () -> String =
            { Uuid.generateV7().toString() }

        commonQueries.initDefaultData(
            cashAccountId = newUuid7(),
            cardAccountId = newUuid7(),
            cashOpId = newUuid7(),
            cardOpId = newUuid7(),
            // categories
            newUuid7(),
            newUuid7(),
            newUuid7(),
            newUuid7(),
            newUuid7(),
            newUuid7(),
        ).await()
    }

    fun getTransactionsFlow(accountId: String?): Flow<List<Transaction>> {
        return transactionsQueries.selectAllWithAccountAndCategory(accountId)
            .asFlow()
            .mapToList(AsyncDispatcher)
            .map { entity ->
                entity.listToDomain()
            }
    }


    fun getTransactionsFilteredFlow(
        accountId: String?,
        filters: TransactionFilters,
        limit: Long
    ): Flow<List<Transaction>> {
        val safeCategoryIds = filters.categoryIds.ifEmpty { listOf("") }
        val safeAccountIds = filters.accountIds.ifEmpty { listOf("") }
        val hasTypeFilter = filters.allowIncome || filters.allowOutcome || filters.allowTransfer

        return transactionsQueries.selectAllFiltered(
            accountId = accountId,
            noteQuery = filters.noteQuery?.takeIf { it.isNotBlank() },
            dateFrom = filters.dateFrom,
            dateTo = filters.dateTo,
            hasCategoryFilter = if (filters.categoryIds.isNotEmpty()) 1 else 0,
            categoryIds = safeCategoryIds,
            hasAccountFilter = if (filters.accountIds.isNotEmpty()) 1 else 0,
            accountIds = safeAccountIds,
            hasTypeFilter = if (hasTypeFilter) 1 else 0,
            allowIncome = if (filters.allowIncome) 1 else 0,
            allowOutcome = if (filters.allowOutcome) 1 else 0,
            allowTransfer = if (filters.allowTransfer) 1 else 0,
            limit = limit
        ).asFlow().mapToList(AsyncDispatcher).map { it.listToDomain() }
    }

    suspend fun selectPrimaryTransaction(id: String) =
        transactionsQueries.selectPrimaryWithAccountAndCategoryById(id).awaitAsOne().toDomain()

    @OptIn(ExperimentalUuidApi::class)
    suspend fun upsertTransaction(transaction: Transaction) {
        transactionsQueries.transaction {
            // Смотрим только если операция уже была (transactionId != null)
            val oldOps = if (transaction.id != null) transactionsQueries.selectRelatedOperations(transaction.id!!)
                .awaitAsList() else listOf()

            // Откат счетов
            val oldTransferId = transaction.id?.let {
                oldOps.forEach { op ->
                    accountsQueries.updateAccountBalance(delta = -op.amount, id = op.account_uuid)
                }
                oldOps.firstOrNull()?.transfer_id
            }

            // Если трансфер, то берём только отрицательное число – т.е. у отправителя, иначе любое подходящее
            val primaryId =
                oldOps.firstOrNull { it.amount.isNegative || (it.transfer_id == null) }?.id ?: Uuid.generateV7()
                    .toString()

            // Обновление счетов, новые транзакции
            applyNewTransaction(primaryId, transaction.copy(id = primaryId), oldTransferId, oldOps)

            // Если раньше это был перевод, а теперь нет
            if (oldTransferId != null && transaction.type !is TransactionType.Transfer) {
                // Удалит только вторую операцию перевода, т.к. главная уже обновилась (applyNewTransaction)
                transactionsQueries.deleteByTransferId(oldTransferId)
                // Удаление записи из Transaction
                transfersQueries.delete(oldTransferId)
            }
        }
    }

    suspend fun deleteTransaction(id: String) {
        transactionsQueries.transaction {
            val relatedOps = transactionsQueries.selectRelatedOperations(id).awaitAsList()

            if (relatedOps.isEmpty()) return@transaction

            // Откатываем балансы и удаляем связанные операции
            relatedOps.forEach { op ->
                accountsQueries.updateAccountBalance(
                    delta = -op.amount,
                    id = op.account_uuid
                )

                transactionsQueries.delete(op.id)
            }

            // Если был трансфер – удаляем его
            relatedOps.firstOrNull()?.transfer_id?.let { transferId ->
                transfersQueries.delete(transferId)
            }
        }
    }

    private suspend fun applyNewTransaction(
        primaryId: String,
        transaction: Transaction,
        oldTransferId: String?,
        relatedOps: List<Operations>
    ) {
        when (val type = transaction.type) {
            is TransactionType.Transfer -> applyTransfer(primaryId, transaction, type, oldTransferId, relatedOps)
            else -> {
                val amount = transaction.value * (if (type is TransactionType.Income) 1 else -1)
                accountsQueries.updateAccountBalance(delta = amount, id = transaction.account.id)

                transactionsQueries.upsertFrom(
                    transaction.copy(
                        value = amount
                    ), transferId = null
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun applyTransfer(
        primaryId: String,
        transaction: Transaction,
        type: TransactionType.Transfer,
        oldTransferId: String?,
        relatedOps: List<Operations>
    ) {
        val transferId = oldTransferId ?: Uuid.generateV7().toString()

        transfersQueries.upsertFrom(transaction, transferId)

        // Списание (Отправитель) – всегда привязана к primaryId
        val senderAmount = -transaction.value
        accountsQueries.updateAccountBalance(delta = senderAmount, id = type.from.id)
        transactionsQueries.upsert(
            id = primaryId,
            account_uuid = type.from.id,
            category_uuid = null,
            transfer_id = transferId,
            amount = senderAmount,
            date = transaction.date.toInstant(),
            notes = transaction.note
        )

        // Зачисление (Получатель)
        val receiverAmount = transaction.value
        accountsQueries.updateAccountBalance(delta = receiverAmount, id = type.to.id)

        val secondaryId = relatedOps.firstOrNull { it.id != primaryId }?.id ?: Uuid.generateV7().toString()
        transactionsQueries.upsert(
            id = secondaryId,
            account_uuid = type.to.id,
            category_uuid = null,
            transfer_id = transferId,
            amount = receiverAmount,
            date = transaction.date.toInstant(),
            notes = transaction.note
        )
    }
}

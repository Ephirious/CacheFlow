package transactions.repositories

import data.TriggersQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import transactions.db.*
import transactions.local.TransactionsLocalDataSource
import transactions.models.Transaction
import transactions.models.TransactionFilters
import utils.Logg
import utils.presentation.AsyncDispatcher
import kotlin.coroutines.EmptyCoroutineContext

class TransactionsRepositoryImpl(
    private val dbDataSource: TransactionsDatabaseDataSource,
    private val localDataSource: TransactionsLocalDataSource,

    private val triggersQueries: TriggersQueries,

    ) : TransactionsRepository {
    init {
        CoroutineScope(EmptyCoroutineContext).launch(AsyncDispatcher) {
            setDbOnFirstEntranceAndTriggers(false)
        }
    }


    override suspend fun setDbOnFirstEntranceAndTriggers(force: Boolean) {
        triggersQueries.createTriggers().await()
        setDbOnFirstEntrance(force = force)
    }

    suspend fun setDbOnFirstEntrance(force: Boolean) {
        if (!localDataSource.getFirstEntrance() || force) {
            try {
                dbDataSource.initBase()
                localDataSource.setFirstEntrance()
            } catch (e: Exception) {
                Logg.error { e.message }
            }
        }
    }

    override fun unsetFirstEntrance() {
        localDataSource.unsetFirstEntrance()
    }

    override fun getFilteredTransactionsFlow(
        accountId: String?, filters: TransactionFilters, limit: Long
    ): Flow<List<Transaction>> =
        dbDataSource.getTransactionsFilteredFlow(accountId, filters, limit).flowOn(AsyncDispatcher)

    override fun getTransactionsFlow(accountId: String?): Flow<List<Transaction>> =
        dbDataSource.getTransactionsFlow(accountId).flowOn(AsyncDispatcher)


    override suspend fun upsertTransaction(transaction: Transaction) {
        dbDataSource.upsertTransaction(transaction)
    }

    override suspend fun deleteTransaction(id: String) {
        dbDataSource.deleteTransaction(id)
    }

    override suspend fun selectTransactionById(id: String): Transaction =
        dbDataSource.selectPrimaryTransaction(id)


    // not safety extensions

    override suspend fun hardDeleteTransaction(id: String) {
        dbDataSource.hardDeleteTransaction(id)
    }

    override suspend fun hardDeleteTransfer(id: String) {
        dbDataSource.hardDeleteTransfer(id)
    }

    override suspend fun badInsertTransaction(
        id: String,
        accountUuid: String,
        transferId: String?,
        categoryId: String?,
        amount: String,
        date: String,
        notes: String
    ) {
        dbDataSource.badInsertTransaction(
            id = id,
            accountUuid = accountUuid,
            transferId = transferId,
            categoryId = categoryId,
            amount = amount,
            date = date,
            notes = notes
        )
    }

    override suspend fun badInsertTransfer(
        id: String,
        accountFromId: String,
        accountToId: String
    ) {
        dbDataSource.badInsertTransfer(
            id = id,
            accountFromId = accountFromId,
            accountToId = accountToId
        )
    }

}
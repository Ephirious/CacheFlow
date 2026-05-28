package transactions.repositories

import kotlinx.coroutines.flow.Flow
import transactions.models.Transaction

interface TransactionsRepository {
    fun getTransactionsFlow(accountId: String?): Flow<List<Transaction>>

    suspend fun upsertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: String)

    suspend fun selectTransactionById(id: String): Transaction


    // МЕТОДЫ НИЖЕ ИСПОЛЬЗОВАТЬ АККУРАТНО – могут от*****ть
    suspend fun hardDeleteTransaction(id: String)
    suspend fun hardDeleteTransfer(id: String)

    suspend fun badInsertTransaction(
        id: String,
        accountUuid: String,
        transferId: String?,
        categoryId: String?,
        amount: String,
        date: String,
        notes: String
    )

    suspend fun badInsertTransfer(
        id: String,
        accountFromId: String,
        accountToId: String
    )

    suspend fun setDbOnFirstEntranceAndTriggers(force: Boolean)


    fun unsetFirstEntrance()
}

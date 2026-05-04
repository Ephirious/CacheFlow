package transactions.repositories

import kotlinx.coroutines.flow.Flow
import transactions.models.Transaction

interface TransactionsRepository {
    fun getTransactionsFlow(accountId: String?): Flow<List<Transaction>>

    suspend fun upsertTransaction(transaction: Transaction)

    suspend fun selectTransactionById(id: String): Transaction
}
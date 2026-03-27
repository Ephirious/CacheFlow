package transactions.repositories

import kotlinx.coroutines.flow.Flow
import transactions.models.Transaction

interface TransactionsRepository {
    fun getTransactionsFlow(): Flow<List<Transaction>>

    //    suspend fun getTransactions(): Any
    suspend fun upsertTransaction(transaction: Transaction)
}
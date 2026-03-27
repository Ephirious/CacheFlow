package transactions.repositories

import kotlinx.coroutines.flow.Flow
import sync.repositories.SyncManager
import transactions.db.TransactionsDatabaseDataSource
import transactions.models.Transaction

class TransactionsRepositoryImpl(
    private val dbDataSource: TransactionsDatabaseDataSource,
    private val syncManager: SyncManager
) : TransactionsRepository {
    override fun getTransactionsFlow(): Flow<List<Transaction>> =
        dbDataSource.getTransactionsFlow()

    override suspend fun upsertTransaction(transaction: Transaction) {
        dbDataSource.upsertTransaction(transaction)
        syncManager.requestSync()
    }

}
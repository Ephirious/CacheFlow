package transactions.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import sync.repositories.SyncManager
import transactions.db.TransactionsDatabaseDataSource
import transactions.local.TransactionsLocalDataSource
import transactions.models.Transaction
import utils.presentation.AsyncDispatcher

class TransactionsRepositoryImpl(
    private val dbDataSource: TransactionsDatabaseDataSource,
    private val localDataSource: TransactionsLocalDataSource,
    private val syncManager: SyncManager
) : TransactionsRepository {
    init {
        if (localDataSource.getFirstEntrance()) {
            CoroutineScope(AsyncDispatcher).launch {
                dbDataSource.initBase()
                localDataSource.setFirstEntrance()
            }
        }
    }

    override fun getTransactionsFlow(): Flow<List<Transaction>> =
        dbDataSource.getTransactionsFlow()

    override suspend fun upsertTransaction(transaction: Transaction) {
        dbDataSource.upsertTransaction(transaction)
        syncManager.requestSync()
    }

}
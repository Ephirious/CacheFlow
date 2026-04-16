package transactions.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import sync.repositories.SyncManager
import transactions.db.TransactionsDatabaseDataSource
import transactions.local.TransactionsLocalDataSource
import transactions.models.Transaction
import utils.Logg
import utils.presentation.AsyncDispatcher
import kotlin.coroutines.EmptyCoroutineContext

class TransactionsRepositoryImpl(
    private val dbDataSource: TransactionsDatabaseDataSource,
    private val localDataSource: TransactionsLocalDataSource,
    private val syncManager: SyncManager
) : TransactionsRepository {
    init {
        if (!localDataSource.getFirstEntrance()) {
            CoroutineScope(EmptyCoroutineContext).launch(AsyncDispatcher) {
                try {
                    dbDataSource.initBase()
                    localDataSource.setFirstEntrance()
                } catch (e: Exception) {
                    Logg.error { e.message }
                }
            }
        }
    }

    override fun getTransactionsFlow(accountId: String?): Flow<List<Transaction>> =
        dbDataSource.getTransactionsFlow(accountId).flowOn(AsyncDispatcher)

    override suspend fun upsertTransaction(transaction: Transaction) {
        dbDataSource.upsertTransaction(transaction)
        syncManager.requestSync()
    }

}
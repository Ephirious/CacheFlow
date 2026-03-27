package transactions.repositories

import editors.models.Account
import editors.models.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import sync.repositories.SyncManager
import transactions.db.TransactionsDatabaseDataSource
import transactions.local.TransactionsLocalDataSource
import transactions.models.Transaction
import transactions.models.TransactionType
import utils.Logg
import utils.presentation.AsyncDispatcher
import utils.toLocalDate
import utils.types.BigDecimal
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Clock

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

    override fun getTransactionsFlow(): Flow<List<Transaction>> =
        dbDataSource.getTransactionsFlow().combine(flow<List<Transaction>> {
            emit(
                listOf(
                    Transaction(
                        id = "meow",
                        value = BigDecimal("1000"),
                        type = TransactionType.Income(
                            category = Category(
                                id = "meow",
                                name = "category"
                            ),
                        ),
                        account = Account(
                            id = "meow",
                            title = "account",
                            balance = BigDecimal("100000")
                        ),
                        note = "",
                        date = Clock.System.now().toLocalDate()
                    )
                )
            )
        }) { x, y ->
            x + y
        }.flowOn(AsyncDispatcher)

    override suspend fun upsertTransaction(transaction: Transaction) {
        dbDataSource.upsertTransaction(transaction)
        syncManager.requestSync()
    }

}
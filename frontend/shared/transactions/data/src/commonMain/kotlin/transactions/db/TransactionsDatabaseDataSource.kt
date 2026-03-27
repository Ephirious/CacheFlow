package transactions.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.Accounts
import data.AccountsQueries
import data.CommonQueries
import data.OperationsQueries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import transactions.mappers.listToDomain
import transactions.mappers.toData
import transactions.models.Transaction
import transactions.models.TransactionType
import utils.presentation.AsyncDispatcher
import utils.types.BigDecimal
import kotlin.time.Clock

class TransactionsDatabaseDataSource(
    private val transactionsQueries: OperationsQueries,
    private val accountsQueries: AccountsQueries,
    private val commonQueries: CommonQueries
) {

    // не нашёл лучшего места...
    suspend fun initBase() {
        commonQueries.initDefaultData().await()
    }

    fun getTransactionsFlow(): Flow<List<Transaction>> {
        return transactionsQueries.selectAllWithAccountAndCategory()
            .asFlow()
            .mapToList(AsyncDispatcher)
            .map { entity ->
                entity.listToDomain()
            }
    }


    // TODO: пока только создаём, доделать для редактирования
    suspend fun upsertTransaction(transaction: Transaction) {
        transactionsQueries.transaction {
            val curTime = Clock.System.now()
            val transferId = null

            // TODO: Add Transfer and return id
            // TODO: Update accounts


            val acc = transaction.account.copy(
                balance = transaction.account.balance + transaction.value * (if (transaction.type is TransactionType.Income) BigDecimal(
                    "1"
                ) else BigDecimal("-1"))
            )

            accountsQueries.upsert(
                Accounts(
                    id = acc.id,
                    name = acc.title,
                    funds = acc.balance,
                    created_at = curTime,
                    updated_at = curTime,
                )
            )
            transactionsQueries.upsert(
                transaction.toData(
                    transferId = transferId,
                    createdAt = null,
                    updatedAt = curTime
                )
            )
        }
    }
}
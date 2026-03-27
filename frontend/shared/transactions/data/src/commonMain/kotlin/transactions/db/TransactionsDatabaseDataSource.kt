package transactions.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.OperationsQueries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import transactions.mappers.listToDomain
import transactions.mappers.toData
import transactions.models.Transaction
import utils.presentation.AsyncDispatcher
import kotlin.time.Clock

class TransactionsDatabaseDataSource(
    private val transactionsQueries: OperationsQueries
) {
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
package transactions.usecases

import transactions.models.Transaction
import transactions.repositories.TransactionsRepository

class UpsertTransactionUseCase(
    private val repository: TransactionsRepository,
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.upsertTransaction(transaction)
    }
}
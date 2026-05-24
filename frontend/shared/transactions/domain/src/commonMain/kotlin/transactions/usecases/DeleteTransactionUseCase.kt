package transactions.usecases

import transactions.repositories.TransactionsRepository

class DeleteTransactionUseCase(
    private val repository: TransactionsRepository,
) {
    suspend operator fun invoke(id: String) = repository.deleteTransaction(id)
}
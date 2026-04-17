package transactions.usecases

import transactions.repositories.TransactionsRepository

class GetTransactionUseCase(
    private val repository: TransactionsRepository,
) {
    suspend operator fun invoke(id: String) = repository.selectTransactionById(id)
}
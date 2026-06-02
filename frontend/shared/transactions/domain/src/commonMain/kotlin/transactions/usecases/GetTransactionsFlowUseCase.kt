package transactions.usecases

import transactions.repositories.TransactionsRepository

class GetTransactionsFlowUseCase(
    private val repository: TransactionsRepository,
) {
    operator fun invoke(
        accountId: String?
    ) = repository.getTransactionsFlow(accountId)
}
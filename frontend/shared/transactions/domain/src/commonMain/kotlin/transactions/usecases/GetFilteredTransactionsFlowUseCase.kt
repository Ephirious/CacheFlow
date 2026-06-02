package transactions.usecases

import transactions.models.TransactionFilters
import transactions.repositories.TransactionsRepository

class GetFilteredTransactionsFlowUseCase(
    private val repository: TransactionsRepository,
) {
    operator fun invoke(
        accountId: String?, filters: TransactionFilters, limit: Long
    ) = repository.getFilteredTransactionsFlow(accountId, filters, limit)
}
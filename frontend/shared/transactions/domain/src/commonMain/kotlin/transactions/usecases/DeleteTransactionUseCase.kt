package transactions.usecases

import core_validation.combineStrictRules
import core_validation.data.IdRule
import transactions.repositories.TransactionsRepository

class DeleteTransactionUseCase(
    private val repository: TransactionsRepository,
) {
    suspend operator fun invoke(id: String) {
        combineStrictRules(
            { IdRule.validate(id) }
        )

        repository.deleteTransaction(id)
    }
}
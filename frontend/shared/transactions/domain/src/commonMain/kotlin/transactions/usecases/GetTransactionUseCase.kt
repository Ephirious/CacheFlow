package transactions.usecases

import core_validation.combineStrictRules
import core_validation.data.IdRule
import transactions.models.Transaction
import transactions.repositories.TransactionsRepository

class GetTransactionUseCase(
    private val repository: TransactionsRepository,
) {
    suspend operator fun invoke(id: String): Transaction {
        combineStrictRules(
            { IdRule.validate(id) }
        )

       return repository.selectTransactionById(id)
    }
}
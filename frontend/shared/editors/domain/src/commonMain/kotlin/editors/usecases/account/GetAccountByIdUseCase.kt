package editors.usecases.account

import core_validation.combineStrictRules
import core_validation.data.IdRule
import editors.models.Account
import editors.repositories.AccountsRepository


class GetAccountByIdUseCase(
    private val repository: AccountsRepository,
) {
    suspend operator fun invoke(id: String): Account {
        combineStrictRules(
            { IdRule.validate(id) },
        )

        return repository.getAccountById(id)
    }
}
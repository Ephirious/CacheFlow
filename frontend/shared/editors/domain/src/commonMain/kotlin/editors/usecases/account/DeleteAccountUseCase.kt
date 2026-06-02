package editors.usecases.account

import core_validation.combineStrictRules
import core_validation.data.IdRule
import editors.repositories.AccountsRepository


class DeleteAccountUseCase(
    private val repository: AccountsRepository,
) {
    suspend operator fun invoke(id: String) {
        combineStrictRules(
            { IdRule.validate(id) },
        )
        repository.softDelete(id = id)
    }
}
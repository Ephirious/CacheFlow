package editors.usecases.account

import core_validation.combineStrictRules
import core_validation.data.IdRule
import core_validation.data.account.AccountTitleRule
import editors.repositories.AccountsRepository
import utils.types.HexColor


class EditAccountUseCase(
    private val repository: AccountsRepository,
) {
    suspend operator fun invoke(id: String, name: String, color: HexColor) {
        combineStrictRules(
            { IdRule.validate(id) },
            { AccountTitleRule.validate(name) }
        )
        repository.updateAccount(id = id, name = name, color = color)
    }
}
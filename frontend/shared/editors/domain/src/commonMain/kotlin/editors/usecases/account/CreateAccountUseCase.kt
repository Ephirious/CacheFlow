package editors.usecases.account

import core_validation.combineStrictRules
import core_validation.data.account.AccountInitialBalanceRule
import core_validation.data.account.AccountTitleRule
import editors.repositories.AccountsRepository
import utils.types.HexColor


class CreateAccountUseCase(
    private val repository: AccountsRepository,
) {
    suspend operator fun invoke(name: String, stringAmount: String, color: HexColor) {
        combineStrictRules(
            { AccountTitleRule.validate(name) },
            { AccountInitialBalanceRule.validate(stringAmount) },
        )

        repository.insertAccount(name = name.trim(), stringAmount = stringAmount, color = color)
    }
}
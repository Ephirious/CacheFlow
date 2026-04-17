package editors.usecases.account

import editors.repositories.AccountsRepository
import utils.types.HexColor


class CreateAccountUseCase(
    private val repository: AccountsRepository,
) {
    suspend operator fun invoke(name: String, stringAmount: String, color: HexColor) =
        repository.insertAccount(name = name, stringAmount = stringAmount, color = color)
}
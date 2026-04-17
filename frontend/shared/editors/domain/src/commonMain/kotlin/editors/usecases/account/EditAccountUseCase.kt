package editors.usecases.account

import editors.repositories.AccountsRepository
import utils.types.HexColor


class EditAccountUseCase(
    private val repository: AccountsRepository,
) {
    suspend operator fun invoke(id: String, name: String, color: HexColor) =
        repository.updateAccount(id = id, name = name, color = color)
}
package editors.usecases.account

import editors.repositories.AccountsRepository


class DeleteAccountUseCase(
    private val repository: AccountsRepository,
) {
    suspend operator fun invoke(id: String) =
        repository.softDelete(id = id)
}
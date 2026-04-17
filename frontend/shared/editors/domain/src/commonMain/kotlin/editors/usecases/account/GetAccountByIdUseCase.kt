package editors.usecases.account

import editors.repositories.AccountsRepository


class GetAccountByIdUseCase(
    private val repository: AccountsRepository,
) {
    suspend operator fun invoke(id: String) = repository.getAccountById(id)
}
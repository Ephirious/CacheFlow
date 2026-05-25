package editors.usecases.account

import editors.repositories.AccountsRepository


class GetAccountsFlowUseCase(
    private val repository: AccountsRepository,
) {
    operator fun invoke(onlyActive: Boolean = true) = repository.getAccountsFlow(onlyActive)
}
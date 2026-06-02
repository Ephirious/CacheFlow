package auth.usecases

import auth.AuthRepository
import core_validation.combineStrictRules
import core_validation.data.auth.AuthEmailRule
import core_validation.data.auth.AuthPasswordRule
import sync.repositories.SyncManager

class LoginUseCase(
    private val repository: AuthRepository,
    private val syncManager: SyncManager,
) {
    suspend operator fun invoke(email: String, password: String) {
        combineStrictRules(
            { AuthEmailRule.validate(email) },
            { AuthPasswordRule.validate(password) }
        )

        repository.login(email = email, password = password)
        repository.getProfile()

        repository.clearAllTables()
        syncManager.forceSync(retry = true)
    }
}
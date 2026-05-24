package auth.usecases

import auth.AuthRepository
import sync.repositories.SyncManager

class LoginUseCase(
    private val repository: AuthRepository,
    private val syncManager: SyncManager,
) {
    suspend operator fun invoke(email: String, password: String) {
        repository.login(email = email, password = password)
        repository.getProfile()

        syncManager.forceSync(retry = true)
    }
}
package auth.usecases

import auth.AuthRepository
import auth.UserId
import sync.repositories.SyncManager

class VerifyRegistrationUseCase(
    private val repository: AuthRepository,
    private val syncManager: SyncManager,
) {
    suspend operator fun invoke(userId: UserId, code: String, email: String, password: String) {
        repository.verifyRegistration(userId = userId, verificationCode = code)
        repository.login(email = email, password = password)
        repository.getProfile()

        syncManager.forceSync()
    }
}
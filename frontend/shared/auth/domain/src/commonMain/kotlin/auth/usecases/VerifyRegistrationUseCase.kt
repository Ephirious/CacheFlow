package auth.usecases

import auth.AuthRepository
import auth.UserId

class VerifyRegistrationUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: UserId, code: String) =
        repository.verifyRegistration(userId = userId, verificationCode = code)
}
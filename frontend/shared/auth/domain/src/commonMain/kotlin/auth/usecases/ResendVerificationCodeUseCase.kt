package auth.usecases

import auth.AuthRepository
import auth.UserId

class ResendVerificationCodeUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: UserId) =
        repository.resendVerificationCode(userId = userId)
}
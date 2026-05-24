package auth.usecases

import auth.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() =
        repository.logout()
}
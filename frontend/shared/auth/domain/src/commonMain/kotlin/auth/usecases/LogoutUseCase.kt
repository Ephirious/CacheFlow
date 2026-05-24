package auth.usecases

import auth.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke() =
        repository.logout()
}
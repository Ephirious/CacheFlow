package auth.usecases

import auth.AuthRepository

class GetProfileUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() =
        repository.getProfile()
}
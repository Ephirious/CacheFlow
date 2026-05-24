package auth.usecases

import auth.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String) =
        repository.register(email = email, password = password, name = name)
}
package auth.usecases

import auth.AuthRepository
import core_validation.combineStrictRules
import core_validation.data.auth.AuthEmailRule
import core_validation.data.auth.AuthPasswordRule
import core_validation.data.auth.AuthUsernameRule

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String): String {
        combineStrictRules(
            { AuthEmailRule.validate(email, Unit, null) },
            { AuthPasswordRule.validate(password, Unit, null) },
            { AuthUsernameRule.validate(name, Unit, null) },
        )
        return repository.register(email = email, password = password, name = name)
    }
}
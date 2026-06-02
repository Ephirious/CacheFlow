package auth.usecases

import auth.AuthRepository
import auth.UserId
import core_validation.combineStrictRules
import core_validation.data.auth.AuthEmailRule
import core_validation.data.auth.AuthOTPCodeRule
import core_validation.data.auth.AuthPasswordRule
import sync.repositories.SyncManager

class VerifyRegistrationUseCase(
    private val repository: AuthRepository,
    private val syncManager: SyncManager,
) {
    suspend operator fun invoke(userId: UserId, code: String, email: String, password: String) {
        combineStrictRules(
            { AuthOTPCodeRule.validate(code) },

            // По идее нижнее нет смысла чекать, но лан
            { AuthEmailRule.validate(email) },
            { AuthPasswordRule.validate(password) },
        )

        repository.verifyRegistration(userId = userId, verificationCode = code)


        repository.login(email = email, password = password)
        repository.getProfile()

        syncManager.forceSync(retry = true)
    }
}
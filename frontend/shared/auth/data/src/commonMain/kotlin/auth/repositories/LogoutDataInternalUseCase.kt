package auth.repositories

import auth.TokenStorage
import auth.local.AuthLocalDataSource

class LogoutDataInternalUseCase(private val tokenStorage: TokenStorage, private val localDataSource: AuthLocalDataSource) {
    operator fun invoke() {
        tokenStorage.clearTokens()
        localDataSource.clearProfile()
    }
}
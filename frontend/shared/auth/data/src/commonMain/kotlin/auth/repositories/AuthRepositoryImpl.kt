package auth.repositories

import auth.AuthRepository
import auth.TokenStorage
import auth.UserId
import auth.cloud.AuthRemoteDataSource
import auth.cloud.dtos.ResendCodeRequestDTO
import auth.cloud.dtos.UserCreateDTO
import auth.cloud.dtos.VerifyEmailRequestDTO
import auth.db.AuthDatabaseDataSource
import auth.local.AuthLocalDataSource
import auth.models.Profile
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
    private val authDatabaseDataSource: AuthDatabaseDataSource,
    private val logoutDataInternalUseCase: LogoutDataInternalUseCase,
    private val tokenStorage: TokenStorage,
) : AuthRepository {


    private var shouldRequestProfileFromServer: Boolean = true

    override suspend fun register(email: String, password: String, name: String): UserId =
        remoteDataSource.register(
            UserCreateDTO(
                email = email,
                name = name,
                password = password
            )
        )

    override suspend fun verifyRegistration(userId: UserId, verificationCode: String) {
        remoteDataSource.verifyRegistration(
            VerifyEmailRequestDTO(
                userId = userId,
                code = verificationCode
            )
        )
    }

    override suspend fun resendVerificationCode(userId: UserId) =
        remoteDataSource.resendVerificationCode(ResendCodeRequestDTO(userId = userId))

    override suspend fun clearAllTables() {
        authDatabaseDataSource.clearAllTables()
    }

    override suspend fun login(email: String, password: String) =
        remoteDataSource.login(email = email, password = password)

    override fun logout() {
        logoutDataInternalUseCase()
        remoteDataSource.httpClient.authProviders
            .filterIsInstance<BearerAuthProvider>()
            .forEach { it.clearToken() }
    }

    override suspend fun getProfile(): Profile {
        val offlineData = localDataSource.getProfile()

        if (offlineData == null || shouldRequestProfileFromServer) {
            try {
                val profile = remoteDataSource.getProfile().toDomain()
                shouldRequestProfileFromServer = false
                localDataSource.setProfile(profile)
                return profile
            } catch (e: Exception) {

                // Всё ок – просто нет инета
                if (!tokenStorage.isTokensEmpty() && offlineData != null) {
                    return offlineData
                } else { // Сессия устарела – выходим из акка
                    throw e
                }
            }
        }

        return offlineData
    }

}
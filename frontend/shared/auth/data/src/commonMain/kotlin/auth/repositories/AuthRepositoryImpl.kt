package auth.repositories

import auth.AuthRepository
import auth.UserId
import auth.cloud.AuthRemoteDataSource
import auth.cloud.dtos.ResendCodeRequestDTO
import auth.cloud.dtos.UserCreateDTO
import auth.cloud.dtos.VerifyEmailRequestDTO

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
) : AuthRepository {

    override suspend fun register(email: String, password: String, name: String): UserId =
        remoteDataSource.register(
            UserCreateDTO(
                email = email,
                name = name,
                password = password
            )
        )

    override suspend fun verifyRegistration(userId: UserId, verificationCode: String) =
        remoteDataSource.verifyRegistration(
            VerifyEmailRequestDTO(
                userId = userId,
                code = verificationCode
            )
        )

    override suspend fun resendVerificationCode(userId: UserId) =
        remoteDataSource.resendVerificationCode(ResendCodeRequestDTO(userId = userId))

    override suspend fun login(email: String, password: String) =
        remoteDataSource.login(email = email, password = password)

    override suspend fun logout() =
        remoteDataSource.logout()

    override suspend fun getProfile(): Any =
        remoteDataSource.getProfile() // .toDomain()

}
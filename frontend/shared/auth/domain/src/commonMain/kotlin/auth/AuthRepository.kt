package auth


typealias UserId = String

interface AuthRepository {

    suspend fun register(email: String, password: String, name: String): UserId
    suspend fun verifyRegistration(userId: UserId, verificationCode: String)
    suspend fun resendVerificationCode(userId: UserId)


    suspend fun login(email: String, password: String)
    suspend fun logout()

    suspend fun getProfile(): Any // TODO: ProfileData
}
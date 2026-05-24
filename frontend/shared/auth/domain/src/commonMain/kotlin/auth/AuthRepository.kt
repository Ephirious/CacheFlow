package auth

import auth.models.Profile


typealias UserId = String

interface AuthRepository {

    suspend fun register(email: String, password: String, name: String): UserId
    suspend fun verifyRegistration(userId: UserId, verificationCode: String)
    suspend fun resendVerificationCode(userId: UserId)


    suspend fun login(email: String, password: String)
    fun logout()

    suspend fun getProfile(): Profile
}
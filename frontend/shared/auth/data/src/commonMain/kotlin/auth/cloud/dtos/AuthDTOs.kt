package auth.cloud.dtos

import auth.models.Profile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenDTO(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
// Response from server
data class UserDataDTO(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("created_at") val createdAt: String
) {
    fun toDomain() = Profile(
        id = id,
        name = name,
        email = maskEmail(email),
    )

    private fun maskEmail(originalEmail: String): String {
        val parts = originalEmail.split("@")
        if (parts.size != 2) return originalEmail

        val localPart = parts[0]
        val domainPart = parts[1]

        val maskedLocalPart = when {
            localPart.length <= 3 -> {
                localPart.first() + "***"
            }

            localPart.length <= 5 -> {
                localPart.take(2) + "***" + localPart.last()
            }

            else -> {
                val prefix = localPart.take(3)
                val postfix = localPart.takeLast(2)
                "$prefix***$postfix"
            }
        }

        return "$maskedLocalPart@$domainPart"
    }
}

@Serializable
// Request
data class UserCreateDTO(
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("password") val password: String
)

@Serializable
// Request
data class VerifyEmailRequestDTO(
    @SerialName("user_id") val userId: String,
    @SerialName("code") val code: String
)

@Serializable
// Request
data class ResendCodeRequestDTO(
    @SerialName("user_id") val userId: String
)
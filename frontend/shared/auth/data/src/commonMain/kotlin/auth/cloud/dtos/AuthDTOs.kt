package auth.cloud.dtos

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
)

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
package auth.cloud

import auth.AuthUrls
import auth.TokenStorage
import auth.UserId
import auth.cloud.dtos.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import utils.data.throwableToException

class AuthRemoteDataSource(
    val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun register(request: UserCreateDTO): UserId = throwableToException {
        httpClient.post(AuthUrls.REGISTER) {
            setBody(request)
        }.body<UserDataDTO>().id
    }

    suspend fun verifyRegistration(request: VerifyEmailRequestDTO) = throwableToException {
        httpClient.post(AuthUrls.VERIFY_REGISTRATION) {
            setBody(request)
        }
    }

    suspend fun resendVerificationCode(request: ResendCodeRequestDTO) {
        throwableToException {
            httpClient.post(AuthUrls.RESEND_VERIFICATION_CODE) {
                setBody(request)
            }
        }
    }


    suspend fun login(email: String, password: String) = throwableToException {
        // x-www-form-urlencoded, а не json
        val tokens = httpClient.post("/auth/login") {
            setBody(FormDataContent(Parameters.build {
                append("username", email)
                append("password", password)
                append("grant_type", "password")
            }))
        }.body<TokenDTO>()

        tokenStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
    }

    suspend fun getProfile(): UserDataDTO = throwableToException {
        httpClient.post(AuthUrls.GET_PROFILE).body()
    }


}
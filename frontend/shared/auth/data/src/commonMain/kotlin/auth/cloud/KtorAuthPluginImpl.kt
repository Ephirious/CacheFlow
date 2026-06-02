package auth.cloud

import auth.AuthUrls
import auth.cloud.dtos.TokenDTO
import auth.KtorAuthPlugin
import auth.TokenStorage
import auth.repositories.LogoutDataInternalUseCase
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.http.encodedPath
import utils.Logg

class KtorAuthPluginImpl(
    private val tokenStorage: TokenStorage,
    private val logoutDataInternalUseCase: LogoutDataInternalUseCase,
) : KtorAuthPlugin {
    override fun install(config: HttpClientConfig<*>) {
        config.install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = tokenStorage.getAccessToken()
                    val refreshToken = tokenStorage.getRefreshToken()
                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken, refreshToken)
                    } else null
                }

                refreshTokens {
                    val oldRefreshToken = tokenStorage.getRefreshToken() ?: return@refreshTokens null

                    try {
                        val response = client.post(AuthUrls.REFRESH_TOKEN) {
                            bearerAuth(oldRefreshToken)
                        }.body<TokenDTO>()

                        tokenStorage.saveTokens(response.accessToken, response.refreshToken)
                        BearerTokens(response.accessToken, response.refreshToken)
                    } catch (_: Exception) {
                        logoutDataInternalUseCase()
                        Logg.error { "Session expired" }
                        null
                    }
                }

                sendWithoutRequest { request ->
                    val path = request.url.encodedPath
                    !path.contains(AuthUrls.LOGIN) &&
                            !path.contains(AuthUrls.REGISTER) &&
                            !path.contains(AuthUrls.VERIFY_REGISTRATION) &&
                            !path.contains(AuthUrls.RESEND_VERIFICATION_CODE) &&
                            !path.contains(AuthUrls.REFRESH_TOKEN)
                }
            }
        }
    }
}
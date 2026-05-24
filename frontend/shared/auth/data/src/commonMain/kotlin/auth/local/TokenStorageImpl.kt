package auth.local

import auth.models.Token
import auth.TokenStorage
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set


// TODO: зашифровать на Android и iOS (не касается PWA)
class TokenStorageImpl(
    val settings: Settings,
) : TokenStorage {
    override fun getAccessToken(): Token? =
        settings[ACCESS_TOKEN_KEY]

    override fun getRefreshToken(): Token? =
        settings[REFRESH_TOKEN_KEY]

    override fun saveTokens(accessToken: Token, refreshToken: Token) {
        settings[ACCESS_TOKEN_KEY] = accessToken
        settings[REFRESH_TOKEN_KEY] = refreshToken
    }

    override fun clearTokens() {
        settings.remove(ACCESS_TOKEN_KEY)
        settings.remove(REFRESH_TOKEN_KEY)
    }

    companion object {
        const val ACCESS_TOKEN_KEY = "access_token_key"
        const val REFRESH_TOKEN_KEY = "refresh_token_key"
    }
}
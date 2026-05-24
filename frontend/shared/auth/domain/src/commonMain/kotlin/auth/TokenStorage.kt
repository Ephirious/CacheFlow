package auth

import auth.models.Token


interface TokenStorage {
    fun getAccessToken(): Token?
    fun getRefreshToken(): Token?
    fun saveTokens(accessToken: Token, refreshToken: Token)
    fun clearTokens()
}
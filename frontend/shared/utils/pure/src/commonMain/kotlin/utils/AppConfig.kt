package utils

object AppConfig {
    val isDebuggable = getFromEnv("APP_DEBUGGABLE")?.toBooleanStrictOrNull() ?: false
    val pushVapidPublicKey =
        getFromEnv("APP_VAPID_PUBLIC_KEY") ?: "not for mvp =/ (used to be for notifications)"

    // KtorClient
    val serverHost = getFromEnv("SERVER_HOST") ?: "localhost"
    val serverPort: Int? = getFromEnv("SERVER_PORT")?.toIntOrNull()
    val isHttps = getFromEnv("APP_IS_HTTPS")?.toBooleanStrictOrNull() ?: false
}
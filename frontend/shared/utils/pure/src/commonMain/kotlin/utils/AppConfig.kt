package utils

object AppConfig {
    val isDebuggable = getFromEnv("APP_DEBUGGABLE")?.toBooleanStrictOrNull() ?: false
    val pushVapidPublicKey =
        getFromEnv("APP_VAPID_PUBLIC_KEY") ?: "not for mvp =/ (used to be for notifications)"

    // KtorClient
    val serverHost = "cache-flow.ru"
    val serverPort: Int? = getFromEnv("SERVER_PORT")?.toIntOrNull()
    val isHttps = true
}

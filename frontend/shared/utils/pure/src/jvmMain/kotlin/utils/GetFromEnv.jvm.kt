package utils

actual fun getFromEnv(key: String): String? {
    return System.getenv(key)
}
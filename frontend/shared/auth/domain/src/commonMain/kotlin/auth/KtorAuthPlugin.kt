package auth

import io.ktor.client.HttpClientConfig

interface KtorAuthPlugin {
    fun install(config: HttpClientConfig<*>)
}
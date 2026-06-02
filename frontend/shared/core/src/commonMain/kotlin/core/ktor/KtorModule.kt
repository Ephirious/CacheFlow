package core.ktor

import auth.KtorAuthPlugin
import io.ktor.client.HttpClient
import org.koin.dsl.module


internal val ktorModule = module {
    single<HttpClient> {
        getHttpClient(
            HttpEngineFactory().createEngine(),
            configBlock = {
                get<KtorAuthPlugin>().install(this)
            }
        )
    }
}
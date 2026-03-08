package core.ktor

import io.ktor.client.HttpClient
import org.koin.dsl.module


internal val ktorModule = module {
    single<HttpClient> {
        getHttpClient(HttpEngineFactory().createEngine())
    }
}
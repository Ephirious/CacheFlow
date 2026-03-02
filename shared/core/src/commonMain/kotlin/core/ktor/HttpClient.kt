package core.ktor

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
fun getHttpClient(engineFactory: HttpClientEngineFactory<HttpClientEngineConfig>) =
    HttpClient(engineFactory) {
        install(Logging) {
            level = LogLevel.ALL
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 15000
            requestTimeoutMillis = 30000
        }

//        install(ContentNegotiation) {
//            protobuf(protobuf = proto)
//        }
    }
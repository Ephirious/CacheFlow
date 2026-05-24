package core.ktor

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import utils.AppConfig


@OptIn(ExperimentalSerializationApi::class)
fun getHttpClient(
    engineFactory: HttpClientEngineFactory<HttpClientEngineConfig>,
    // AuthFeature
    configBlock: HttpClientConfig<*>.() -> Unit
) =
    HttpClient(engineFactory) {
        install(Logging) {
            level = LogLevel.ALL
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 15000
            requestTimeoutMillis = 30000
        }

        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                classDiscriminatorMode = ClassDiscriminatorMode.POLYMORPHIC
                classDiscriminator = "kotlin_class_type"
            })
        }

        install(DefaultRequest) {
            this.host = AppConfig.serverIP
            this.port = AppConfig.serverPort
        }

        configBlock()
    }
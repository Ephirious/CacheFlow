package core.ktor

internal actual class HttpEngineFactory actual constructor() {
    actual fun createEngine(): io.ktor.client.engine.HttpClientEngineFactory<io.ktor.client.engine.HttpClientEngineConfig> {
        TODO("Not yet implemented")
    }
}
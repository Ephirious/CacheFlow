package sync.cloud

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import sync.cloud.dtos.SyncRequest
import sync.cloud.dtos.SyncResponse

class SyncRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun sendSyncRequest(request: SyncRequest): SyncResponse {
        return httpClient.post("/sync") {
            setBody(request)
            header("Bearer", "") //<- TODO: ARTEM VSTAV SUDA(V PUSTIE KAVICHKI) POLUCHENIE TOKENA
        }.body()
    }
}
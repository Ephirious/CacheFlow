package sync.cloud

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import sync.cloud.dtos.SyncRequest
import sync.cloud.dtos.SyncResponse
import utils.data.throwableToException

class SyncRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun sendSyncRequest(request: SyncRequest): SyncResponse =
        throwableToException {
            return httpClient.post("sync") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }
}
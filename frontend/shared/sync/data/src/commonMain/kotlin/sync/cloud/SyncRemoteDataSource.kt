package sync.cloud

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import sync.cloud.dtos.SyncRequest
import sync.cloud.dtos.SyncResponse
import utils.data.throwableToException

class SyncRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun sendSyncRequest(request: SyncRequest): SyncResponse =
        throwableToException {
            return httpClient.post("sync") {
                setBody(request)
            }.body()
        }
}
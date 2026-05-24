package sync.cloud

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import sync.cloud.dtos.SyncRequest
import sync.cloud.dtos.SyncResponse
import utils.data.throwableToException

class SyncRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun sendSyncRequest(request: SyncRequest): SyncResponse =
        throwableToException {
            val token = TODO() //<- TODO: ARTEM VSTAV SUDA(V PUSTIE KAVICHKI) POLUCHENIE TOKENA

            return httpClient.post("sync") {
                contentType(ContentType.Application.Json)
                setBody(request)
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
        }
}
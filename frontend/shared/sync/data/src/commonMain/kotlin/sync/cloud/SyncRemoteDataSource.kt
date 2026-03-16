package sync.cloud

import io.ktor.client.*
import sync.cloud.dtos.SyncData
import utils.data.throwableToException

class SyncRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun sync(data: SyncData) = throwableToException {
//        httpClient.post()
    }
}
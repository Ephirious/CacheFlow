package sync.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
enum class SyncStatus {
    Ok, InProcess, Failed
}


interface SyncRepository {
    fun resetLastSyncDate()


    suspend fun setSyncLock(isSyncRunning: Boolean)
}

interface SyncManager {

    // Добавляет запрос о синхронизации в очередь – выдерживает debounce перед отправкой
    suspend fun requestSync()
    suspend fun forceSync(retry: Boolean)

    // Статус синхронизации для отображения в UI
    val status: StateFlow<SyncStatus>

    val scope: CoroutineScope
}


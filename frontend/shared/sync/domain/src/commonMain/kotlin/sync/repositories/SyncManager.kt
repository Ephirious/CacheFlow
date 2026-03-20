package sync.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
@JsExport
enum class SyncStatus {
    Ok, InProcess, Failed
}

interface SyncManager {

    // Добавляет запрос о синхронизации в очередь – выдерживает debounce перед отправкой
    suspend fun requestSync()
    suspend fun forceSync()

    // Статус синхронизации для отображения в UI
    val status: StateFlow<SyncStatus>

    val scope: CoroutineScope
}


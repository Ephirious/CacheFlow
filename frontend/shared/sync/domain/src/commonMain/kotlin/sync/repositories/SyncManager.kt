package sync.repositories

import kotlinx.coroutines.flow.StateFlow

@JsExport
enum class SyncStatus {
    Ok, InProcess, Failed
}

interface SyncManager {

    // Добавляет запрос о синхронизации в очередь – выдерживает debounce перед отправкой
    fun requestSync()

    // Статус синхронизации для отображения в UI
    val status: StateFlow<SyncStatus>
}

//@JsExport()
//@JsName("SyncManager")
//interface SyncManagerJS {
//    fun sync(): Promise<Unit>
//
////    fun observeDb(): Promise<Unit>
//
//    val status: JsValue<SyncStatus>
//}



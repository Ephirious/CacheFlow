package sync.repositories

import kotlinx.coroutines.flow.StateFlow

@JsExport
enum class SyncStatus {
    Ok, InProcess, Failed
}

interface SyncManager {


    // Отправить данные на сервер
    suspend fun sync()

    // Подписаться на обновления бд, если упало с ошибкой, то добавить в ServiceWorker
    suspend fun observeDb()

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



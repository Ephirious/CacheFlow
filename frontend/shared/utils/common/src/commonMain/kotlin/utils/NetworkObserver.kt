package utils

import kotlinx.coroutines.flow.Flow
import kotlin.js.JsExport


@JsExport
enum class NetworkStatus {
    Online, Offline
}

interface NetworkObserver {
    val status: Flow<NetworkStatus>
    val isOnline: Boolean
}
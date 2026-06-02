package settings.sync.mvi

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import sync.repositories.SyncStatus
import kotlin.js.JsExport

@JsExport
sealed class SyncOverviewState : MVIState {
    data object Loading : SyncOverviewState()

    data object NotAuthenticated : SyncOverviewState()

    data class Authenticated(
        val name: String,
        val email: String,
        val id: String,
        val syncStatus: SyncStatus,
        val forceSyncCooldownSeconds: Int = 0
    ) : SyncOverviewState()
}


@JsExport
sealed class SyncOverviewIntent : MVIIntent {
    data object ExportCSV : SyncOverviewIntent()
    data object Logout : SyncOverviewIntent()
    data object ForceSync : SyncOverviewIntent()

    data object UpdateAuthStatus : SyncOverviewIntent()
}
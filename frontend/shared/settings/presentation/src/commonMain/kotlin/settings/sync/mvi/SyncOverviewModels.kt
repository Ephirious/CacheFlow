package settings.sync.mvi

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import sync.repositories.SyncStatus
import kotlin.js.JsExport

@JsExport
sealed class SyncOverviewState(
    var isOnline: Boolean
) : MVIState {
    data object Loading : SyncOverviewState(false)

    data object NotAuthenticated : SyncOverviewState(false)

    data class Authenticated(
        val name: String,
        val email: String,
        val id: String,
        val syncStatus: SyncStatus,
    ) : SyncOverviewState(false)
}


@JsExport
sealed class SyncOverviewIntent : MVIIntent {
    data object ExportCSV : SyncOverviewIntent()
    data object Logout : SyncOverviewIntent()
    data object ForceSync : SyncOverviewIntent()

    data object UpdateAuthStatus : SyncOverviewIntent()
}
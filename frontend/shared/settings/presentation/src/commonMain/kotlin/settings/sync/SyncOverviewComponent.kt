package settings.sync

import settings.sync.mvi.SyncOverviewIntent
import settings.sync.mvi.SyncOverviewState
import utils.interop.JsValue
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface SyncOverviewComponent {


    val onAuthenticateClick: () -> Unit

    fun updateAuthStatus()

    @JsName("state")
    val jsState: JsValue<SyncOverviewState>

    @Suppress("unused")
    fun intent(intent: SyncOverviewIntent)
}
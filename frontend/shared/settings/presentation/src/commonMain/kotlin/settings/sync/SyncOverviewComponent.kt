package settings.sync

import settings.sync.mvi.SyncOverviewIntent
import settings.sync.mvi.SyncOverviewState
import utils.interop.JsValue
import kotlin.js.JsName

interface SyncOverviewComponent {



    fun updateAuthStatus()

    @JsName("state")
    val jsState: JsValue<SyncOverviewState>

    @Suppress("unused")
    fun intent(intent: SyncOverviewIntent)
}
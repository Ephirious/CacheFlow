package settings.sync

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinComponent
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import settings.sync.mvi.SyncOverviewContainer
import settings.sync.mvi.SyncOverviewIntent
import settings.sync.mvi.SyncOverviewState
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope

class RealSyncOverviewComponent(
    componentCtx: ComponentContext,
    container: () -> SyncOverviewContainer,
) : SyncOverviewComponent, KoinComponent, ComponentContext by componentCtx,
    Store<SyncOverviewState, SyncOverviewIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override fun updateAuthStatus() {
        intent(SyncOverviewIntent.UpdateAuthStatus)
    }


    override val jsState: JsValue<SyncOverviewState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}

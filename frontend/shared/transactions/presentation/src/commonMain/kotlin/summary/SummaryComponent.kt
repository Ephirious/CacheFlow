package summary

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import summary.mvi.SummaryContainer
import summary.mvi.SummaryState
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName


@JsExport
interface SummaryComponent : ComponentContext {
    @JsName("state")
    val jsState: JsValue<SummaryState>
}

class RealSummaryComponent(
    componentCtx: ComponentContext,
    container: () -> SummaryContainer,
) : SummaryComponent, ComponentContext by componentCtx,
    Store<SummaryState, Nothing, Nothing> by componentCtx.retainedStore(factory = container) {
    override val jsState: JsValue<SummaryState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}
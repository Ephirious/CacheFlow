package interopSample

import com.arkivanov.decompose.ComponentContext
import interopSample.mvi.InteropSampleContainer
import interopSample.mvi.InteropSampleIntent
import interopSample.mvi.InteropSampleState
import pro.respawn.flowmvi.annotation.InternalFlowMVIAPI
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope

@JsExport
interface InteropSampleComponent : ComponentContext {

    @JsName("state")
    val jsState: JsValue<InteropSampleState>

    @Suppress("unused")
    fun intent(intent: InteropSampleIntent)

    @Suppress("unused")
    val num: Int
}


class RealInteropSampleComponent(
    componentCtx: ComponentContext,
    container: () -> InteropSampleContainer,
    override val num: Int,

    ) : InteropSampleComponent, ComponentContext by componentCtx,
    Store<InteropSampleState, InteropSampleIntent, Nothing> by componentCtx.retainedStore(
        factory = container
    ) {

    @OptIn(InternalFlowMVIAPI::class)
    override val jsState: JsValue<InteropSampleState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}

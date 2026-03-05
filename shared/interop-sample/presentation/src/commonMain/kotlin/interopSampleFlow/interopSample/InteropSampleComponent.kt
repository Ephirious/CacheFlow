package interopSampleFlow.interopSample

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.annotation.InternalFlowMVIAPI
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsValue
import utils.interop.asJsValue
import utils.presentation.componentCoroutineScope

@JsExport
interface InteropSampleComponent : ComponentContext {

    @JsName("state")
    val jsState: JsValue<InteropSampleState>

    @Suppress("unused")
    fun intent(intent: InteropSampleIntent)
}


class RealInteropSampleComponent(
    componentCtx: ComponentContext,
    container: () -> InteropSampleContainer
) : InteropSampleComponent, ComponentContext by componentCtx,
    Store<InteropSampleState, InteropSampleIntent, Nothing> by componentCtx.retainedStore(
        factory = container
    ) {

    @OptIn(InternalFlowMVIAPI::class)
    override val jsState: JsValue<InteropSampleState> by lazy {
        states.asJsValue(scope = componentCoroutineScope)
    }
}

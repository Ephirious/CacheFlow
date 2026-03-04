package interopTest

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.annotation.InternalFlowMVIAPI
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.state
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.essenty.dsl.subscribe
import utils.presentation.JsValue
import utils.presentation.asJsValue
import utils.presentation.componentCoroutineScope

@JsExport
interface InteropTestComponent : ComponentContext {
    fun restartState()

    val jsState: JsValue<InteropTestState>

    fun intent(intent: InteropTestIntent)
}


class RealInteropTestComponent(
    componentCtx: ComponentContext,
    container: () -> InteropTestContainer,
) : InteropTestComponent, ComponentContext by componentCtx,
    Store<InteropTestState, InteropTestIntent, Nothing> by componentCtx.retainedStore(
        factory = container
    ) {
    override fun restartState() {
        componentCoroutineScope.launch {
            this@RealInteropTestComponent.closeAndWait()
            this@RealInteropTestComponent.start(componentCoroutineScope)
        }
    }

    @OptIn(InternalFlowMVIAPI::class, DelicateStoreApi::class)
    override val jsState: JsValue<InteropTestState> = states.asJsValue(
        initialValue = state,
        scope = componentCoroutineScope,
    )


}
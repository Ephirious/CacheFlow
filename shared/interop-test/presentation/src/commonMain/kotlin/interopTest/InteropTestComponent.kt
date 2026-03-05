package interopTest

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.annotation.InternalFlowMVIAPI
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsValue
import utils.interop.asJsValue
import utils.presentation.componentCoroutineScope

@JsExport
interface InteropTestComponent : ComponentContext {
    fun restartState()

    @JsName("state")
    val jsState: JsValue<InteropTestState>

    fun intent(intent: InteropTestIntent)

    val num: Int
}


class RealInteropTestComponent(
    componentCtx: ComponentContext,
    container: () -> InteropTestContainer,
    override val num: Int
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
    override val jsState: JsValue<InteropTestState> by lazy {
        states.asJsValue(scope = componentCoroutineScope)
    }
}
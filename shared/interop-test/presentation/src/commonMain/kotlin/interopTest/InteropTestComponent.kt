package interopTest

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.presentation.componentCoroutineScope

@JsExport
interface InteropTestComponent : ComponentContext {
    fun restartState()

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


}
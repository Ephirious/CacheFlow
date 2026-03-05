package interopSampleFlow

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import interopSampleFlow.InteropSampleFlowChild.InteropSampleChild
import interopSampleFlow.InteropSampleFlowConfig.InteropSample
import interopSampleFlow.interopSample.InteropSampleContainer
import interopSampleFlow.interopSample.RealInteropSampleComponent
import pro.respawn.flowmvi.annotation.InternalFlowMVIAPI
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsChildStack
import utils.interop.JsValue
import utils.interop.asJsStack
import utils.interop.asJsValue
import utils.presentation.componentCoroutineScope
import kotlin.getValue

class RealInteropSampleFlowComponent(
    componentCtx: ComponentContext,
    container: () -> InteropSampleFlowContainer
) : InteropSampleFlowComponent, ComponentContext by componentCtx,
    Store<InteropSampleFlowState, InteropSampleFlowIntent, Nothing> by componentCtx.retainedStore(
        factory = container
    ) {

    @OptIn(InternalFlowMVIAPI::class)
    override val jsState: JsValue<InteropSampleFlowState> by lazy {
        states.asJsValue(scope = componentCoroutineScope)
    }
    override val nav = StackNavigation<InteropSampleFlowConfig>()

    private val _stack = childStack(
        source = nav,
        serializer = InteropSampleFlowConfig.serializer(),
        initialConfiguration = InteropSample(1),
        childFactory = ::child,
        handleBackButton = true
    )
    override val stack: Value<ChildStack<InteropSampleFlowConfig, InteropSampleFlowChild>>
        get() = _stack
    override val jsStack: JsValue<JsChildStack<InteropSampleFlowChild>> by lazy { _stack.asJsStack() }

    private fun child(config: InteropSampleFlowConfig, childCtx: ComponentContext): InteropSampleFlowChild {
        return when (config) {
            is InteropSample -> InteropSampleChild(
                RealInteropSampleComponent(
                    componentCtx = childCtx,
                    container = { InteropSampleContainer() }
                )
            )
        }
    }
}
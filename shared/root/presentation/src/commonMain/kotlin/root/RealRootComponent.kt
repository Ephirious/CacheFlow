package root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import interopSampleFlow.RealInteropSampleFlowComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import utils.interop.JsChildStack
import utils.interop.JsValue
import utils.interop.asJsStack


class RealRootComponent(
    componentContext: ComponentContext
) : RootComponent, KoinComponent, ComponentContext by componentContext {


    override val nav = StackNavigation<RootConfig>()
    private val _stack = childStack(
        source = nav,
        serializer = RootConfig.serializer(),
        initialConfiguration = getInitialConfig(),
        childFactory = ::child,
        handleBackButton = true
    )

    override val stack: Value<ChildStack<RootConfig, RootChild>>
        get() = _stack

    override val jsStack: JsValue<JsChildStack<RootChild>> by lazy { _stack.asJsStack() }

    private fun child(config: RootConfig, childCtx: ComponentContext): RootChild {
        return when (config) {
            is RootConfig.InteropTest -> RootChild.InteropSampleFlowChild(
                RealInteropSampleFlowComponent(componentCtx = childCtx, container = get())
            )
        }
    }

    private fun getInitialConfig(): RootConfig {
        return RootConfig.InteropTest(0)
    }

    override fun testPush() {
        nav.pushNew(RootConfig.InteropTest(10 + _stack.backStack.size))
    }
}
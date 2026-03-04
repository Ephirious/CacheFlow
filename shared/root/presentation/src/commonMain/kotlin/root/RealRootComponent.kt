package root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import interopTest.RealInteropTestComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import utils.presentation.JsChildStack
import utils.presentation.JsValue
import utils.presentation.asJsStack


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

    override val stack: Value<ChildStack<RootConfig, RootChild>> = _stack

    override val jsStack: JsValue<JsChildStack<RootChild>> = stack.asJsStack()

    private fun child(config: RootConfig, childCtx: ComponentContext): RootChild {
        return when (config) {
            RootConfig.InteropTest -> RootChild.InteropTestChild(
                RealInteropTestComponent(componentCtx = childCtx, container = get())
            )
        }
    }

    private fun getInitialConfig(): RootConfig {
        return RootConfig.InteropTest
    }

}
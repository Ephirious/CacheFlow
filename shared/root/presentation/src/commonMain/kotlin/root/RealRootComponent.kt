package root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import interopTest.RealInteropTestComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import root.RootComponent.Config



@JsExport
class RealRootComponent(
    componentContext: ComponentContext
) : RootComponent, KoinComponent, ComponentContext by componentContext {




    override val nav = StackNavigation<Config>()
    private val _stack = childStack(
        source = nav,
        serializer = Config.serializer(),
        initialConfiguration = getInitialConfig(),
        childFactory = ::child,
        handleBackButton = true
    )

    override val stack: Value<ChildStack<Config, RootChild>>
        get() = _stack

    private fun child(config: Config, childCtx: ComponentContext): RootChild {
        return when (config) {
            Config.InteropTest -> RootChild.InteropTestChild(
                RealInteropTestComponent(componentCtx = childCtx, container = get())
            )
        }
    }

    private fun getInitialConfig(): Config {
        return Config.InteropTest
    }

}
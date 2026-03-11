package root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.childStackWebNavigation
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import org.koin.core.component.KoinComponent
import root.RootChild.*
import root.outputs.onRootOutput
import utils.Url
import utils.consumePathSegment
import utils.interop.JsChildStack
import utils.interop.JsValue
import utils.interop.asJsStack
import utils.path
import utils.pathSegmentOf


class RealRootComponent(
    componentContext: ComponentContext,
    deepLinkUrl: Url? = null,
) : RootComponent, KoinComponent, ComponentContext by componentContext {

    private val components = PersistentRootComponents(this) // ОБЯЗАТЕЛЬНО ОБЪЯВЛЯТЬ СВЕРХУ – ИНАЧЕ ОШИБКА unified

    override val nav = StackNavigation<RootConfig>()
    private val _stack = childStack(
        source = nav,
        serializer = RootConfig.serializer(),
        initialStack = { getInitialStack(deepLinkUrl) },
        childFactory = ::child,
        handleBackButton = true
    )

    override val stack: Value<ChildStack<RootConfig, RootChild>>
        get() = _stack

    override val jsStack: JsValue<JsChildStack<RootChild>> by lazy { _stack.asJsStack() }


    private fun child(config: RootConfig, childCtx: ComponentContext): RootChild {
        return when (config) {
            is RootConfig.InteropTest -> {
                InteropSampleFlowChild(
                    components.interop.get(childCtx.lifecycle)
                )
            }

            RootConfig.Main -> MainChild(
                components.main.get(childCtx.lifecycle)
            )

            RootConfig.Stats -> StatsChild(
                components.stats.get(childCtx.lifecycle)
            )

            RootConfig.Settings -> SettingsChild(
                components.settings.get(childCtx.lifecycle)
            )
        }
    }

    override fun onOutput(output: RootOutput) = onRootOutput(output)
    override val webNavigation: WebNavigation<*> =
        childStackWebNavigation(
            navigator = nav,
            stack = _stack,
            serializer = RootConfig.serializer(),
            pathMapper = { it.configuration.path() }
        )

    private fun getInitialStack(deepLinkUrl: Url?): List<RootConfig> {
        val (path, _) = deepLinkUrl?.consumePathSegment() ?: return listOf(RootConfig.Main) // _ - childUrl

        return when (path) {
            pathSegmentOf<RootConfig.Stats>() -> listOf(RootConfig.Main, RootConfig.Stats)
            pathSegmentOf<RootConfig.Settings>() -> listOf(RootConfig.Main, RootConfig.Settings)
            pathSegmentOf<RootConfig.InteropTest>() -> listOf(RootConfig.Main, RootConfig.InteropTest)
            else -> listOf(RootConfig.Main)
        }
    }
}
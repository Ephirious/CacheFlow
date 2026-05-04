package root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import main.RealMainComponent
import main.mvi.MainContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import root.RootChild.*
import root.outputs.onRootOutput
import settings.RealSettingsComponent
import settings.mvi.SettingsContainer
import stats.RealStatsComponent
import utils.Url
import utils.consumePathSegment
import utils.interop.JsChildPages
import utils.interop.JsValue
import utils.interop.asJsPages
import utils.path
import utils.pathSegmentOf
import utils.presentation.CustomPagesWebNavigation


class RealRootComponent(
    componentContext: ComponentContext,
    deepLinkUrl: Url? = null,
) : RootComponent, KoinComponent, ComponentContext by componentContext {
    override val nav = PagesNavigation<RootConfig>()
    private val _pages = childPages(
        source = nav,
        serializer = RootConfig.serializer(),
        initialPages = { getInitialPages(deepLinkUrl) },
        childFactory = ::child,
        handleBackButton = false
    )

    override val pages: Value<ChildPages<RootConfig, RootChild>>
        get() = _pages

    override val jsPages: JsValue<JsChildPages<RootChild>> by lazy { _pages.asJsPages() }

    private fun child(config: RootConfig, childCtx: ComponentContext): RootChild {
        return when (config) {

            RootConfig.Main -> MainChild(
                RealMainComponent(childCtx, container = { get<MainContainer>() })
            )

            RootConfig.Stats -> StatsChild(
                RealStatsComponent(childCtx)
            )

            is RootConfig.Settings -> SettingsChild(
                RealSettingsComponent(childCtx, container = { get<SettingsContainer>() }, deepLinkUrl = config.deepLinkUrl)
            )

        }
    }

    override fun onOutput(output: RootOutput) = onRootOutput(output)
    override val webNavigation: WebNavigation<*> =
        CustomPagesWebNavigation(
            navigator = nav,
            pages = _pages,
            serializer = RootConfig.serializer(),
            pathMapper = { config -> config.path() },
            childSelector = { child ->
                when (val inst = child.instance) {
                    is MainChild -> null // TODO
                    is SettingsChild -> inst.component
                    is StatsChild -> null
                }
            }
        )


    private fun getInitialPages(deepLinkUrl: Url?): Pages<RootConfig> {
        val (segment, remainingUrl) = deepLinkUrl?.consumePathSegment() ?: (null to null)

        val selectedConfig = when (segment) {
            pathSegmentOf<RootConfig.Stats>() -> RootConfig.Stats
            pathSegmentOf<RootConfig.Settings>() -> RootConfig.Settings(remainingUrl)
            else -> RootConfig.Main
        }

        return Pages(
            items = RootConfig.list(RootConfig.Settings(remainingUrl)),
            selectedIndex = selectedConfig.index,
        )
    }
}

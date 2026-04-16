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
import stats.RealStatsComponent
import utils.Url
import utils.consumePathSegment
import utils.interop.JsChildPages
import utils.interop.JsValue
import utils.interop.asJsPages
import utils.path
import utils.pathSegmentOf


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

            RootConfig.Settings -> SettingsChild(
                RealSettingsComponent(childCtx)
            )
        }
    }

    override fun onOutput(output: RootOutput) = onRootOutput(output)
    override val webNavigation: WebNavigation<*> =
        CustomPagesWebNavigation(
            navigator = nav,
            pages = _pages,
            serializer = RootConfig.serializer(),
            pathMapper = { config -> config.path() }
        )


    private fun getInitialPages(deepLinkUrl: Url?): Pages<RootConfig> {
        var selectedIndex = RootConfig.Main.INDEX
        if (deepLinkUrl != null) {
            val (path, _) = deepLinkUrl.consumePathSegment()

            selectedIndex = when (path) {
                pathSegmentOf<RootConfig.Stats>() -> RootConfig.Stats.INDEX
                pathSegmentOf<RootConfig.Settings>() -> RootConfig.Settings.INDEX
                else -> RootConfig.Main.INDEX
            }
        }

        return Pages(
            items = listOf(RootConfig.Main, RootConfig.Stats, RootConfig.Settings),
            selectedIndex = selectedIndex
        )
    }
}
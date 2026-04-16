package settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.*
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import settings.SettingsChild.AccountsChild
import settings.SettingsChild.CategoriesChild
import settings.pages.accounts.RealAccountsComponent
import settings.pages.categories.RealCategoriesComponent
import utils.Url
import utils.consumePathSegment
import utils.interop.JsChildPages
import utils.interop.JsValue
import utils.interop.asJsPages
import utils.path
import utils.pathSegmentOf
import utils.presentation.CustomPagesWebNavigation


class RealSettingsComponent(
    componentCtx: ComponentContext,
    deepLinkUrl: Url? = null,
//    container: () -> MoreContainer,
) : SettingsComponent, ComponentContext by componentCtx {
//    Store<MoreState, MoreIntent, Nothing> by componentCtx.retainedStore(factory = container)

    //    @OptIn(InternalFlowMVIAPI::class)
//    override val jsState: JsValue<MoreState> by lazy {
//        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
//    }
    override val nav = PagesNavigation<SettingsConfig>()
    private val _pages = childPages(
        source = nav,
        serializer = SettingsConfig.serializer(),
        initialPages = { getInitialPages(deepLinkUrl) },
        childFactory = ::child,
        handleBackButton = false
    )


    override val pages: Value<ChildPages<SettingsConfig, SettingsChild>>
        get() = _pages

    override val jsPages: JsValue<JsChildPages<SettingsChild>> by lazy { _pages.asJsPages() }


    override val webNavigation: WebNavigation<*> =
        CustomPagesWebNavigation(
            navigator = nav,
            pages = _pages,
            serializer = SettingsConfig.serializer(),
            pathMapper = { config -> config.path() }
        )


    private fun child(config: SettingsConfig, childCtx: ComponentContext): SettingsChild {
        return when (config) {
            SettingsConfig.Accounts -> AccountsChild(
                component = RealAccountsComponent(childCtx)
            )

            SettingsConfig.Categories -> CategoriesChild(
                component = RealCategoriesComponent(childCtx)
            )
        }
    }

    private fun getInitialPages(deepLinkUrl: Url?): Pages<SettingsConfig> {

        val (segment, _) = deepLinkUrl?.consumePathSegment() ?: (null to null)

        val selectedConfig = when (segment) {
            pathSegmentOf<SettingsConfig.Categories>() -> SettingsConfig.Categories
            pathSegmentOf<SettingsConfig.Accounts>() -> SettingsConfig.Accounts
            else -> SettingsConfig.Categories
        }

        return Pages(
            items = SettingsConfig.list,
            selectedIndex = selectedConfig.index,
        )
    }

    override fun onOutput(output: SettingsOutput) {
        when (output) {
            SettingsOutput.NavigateToAccounts -> nav.select(SettingsConfig.Accounts.index)
            SettingsOutput.NavigateToCategories -> nav.select(SettingsConfig.Categories.index)
        }
    }
}
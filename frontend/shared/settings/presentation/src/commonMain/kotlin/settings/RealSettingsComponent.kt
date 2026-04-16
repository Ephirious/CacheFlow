package settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.*
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import settings.SettingsChild.AccountsChild
import settings.SettingsChild.CategoriesChild
import settings.pages.accounts.RealAccountsComponent
import settings.pages.categories.RealCategoriesPagesComponent
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
) : SettingsComponent, KoinComponent, ComponentContext by componentCtx {
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
            pathMapper = { config -> config.path() },
            childSelector = { child ->
                when(val inst = child.instance) {
                    is AccountsChild -> null
                    is CategoriesChild -> inst.component
                }
            }
        )


    private fun child(config: SettingsConfig, childCtx: ComponentContext): SettingsChild {
        return when (config) {
            SettingsConfig.Accounts -> AccountsChild(
                component = RealAccountsComponent(childCtx)
            )

            is SettingsConfig.Categories -> CategoriesChild(
                component = RealCategoriesPagesComponent(
                    childCtx,
                    deepLinkUrl = config.deepLinkUrl,
                    getCategoriesFlowUseCase = get()
                )
            )
        }
    }

    private fun getInitialPages(deepLinkUrl: Url?): Pages<SettingsConfig> {

        val (segment, remainingUrl) = deepLinkUrl?.consumePathSegment() ?: (null to null)

        val selectedConfig = when (segment) {
            pathSegmentOf<SettingsConfig.Categories>() -> SettingsConfig.Categories(remainingUrl)
            pathSegmentOf<SettingsConfig.Accounts>() -> SettingsConfig.Accounts
            else -> SettingsConfig.Categories(remainingUrl)
        }

        return Pages(
            items = SettingsConfig.list(SettingsConfig.Categories(remainingUrl)),
            selectedIndex = selectedConfig.index,
        )
    }

    override fun onOutput(output: SettingsOutput) {
        when (output) {
            SettingsOutput.NavigateToAccounts -> nav.select(SettingsConfig.Accounts.index)
            SettingsOutput.NavigateToCategories -> nav.select(SettingsConfig.Categories(null).index)
        }
    }
}
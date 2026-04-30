package settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.*
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import editors.accounts.RealCreateAccountComponent
import editors.accounts.RealEditAccountComponent
import editors.accounts.mvi.CreateAccountContainer
import editors.accounts.mvi.EditAccountContainer
import editors.categories.RealCreateCategoryComponent
import editors.categories.RealEditCategoryComponent
import editors.categories.mvi.CreateCategoryContainer
import editors.categories.mvi.EditCategoryContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.essenty.dsl.subscribe
import settings.SettingsChild.AccountsChild
import settings.SettingsChild.CategoriesChild
import settings.modals.SettingsModalChild
import settings.modals.SettingsModalChild.CreateAccountChild
import settings.modals.SettingsModalChild.CreateCategoryChild
import settings.modals.SettingsModalChild.EditAccountChild
import settings.modals.SettingsModalChild.EditCategoryChild
import settings.modals.SettingsModalConfig
import settings.mvi.SettingsAction
import settings.mvi.SettingsContainer
import settings.mvi.SettingsIntent
import settings.mvi.SettingsState
import settings.pages.accounts.RealAccountsComponent
import settings.pages.categories.RealCategoriesPagesComponent
import utils.Url
import utils.consumePathSegment
import utils.interop.JsChildPages
import utils.interop.JsChildSlot
import utils.interop.JsValue
import utils.interop.asJsPages
import utils.interop.asJsSlot
import utils.interop.jsStateSubscribe
import utils.path
import utils.pathSegmentOf
import utils.presentation.CustomPagesWebNavigation
import utils.presentation.componentCoroutineScope


class RealSettingsComponent(
    componentCtx: ComponentContext,
    container: () -> SettingsContainer,
    deepLinkUrl: Url? = null,
) : SettingsComponent, KoinComponent, ComponentContext by componentCtx,
    Store<SettingsState, SettingsIntent, SettingsAction> by componentCtx.retainedStore(factory = container) {


    override val jsState: JsValue<SettingsState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }

    private val modalNavigation = SlotNavigation<SettingsModalConfig>()


    private val modalSlot: Value<ChildSlot<SettingsModalConfig, SettingsModalChild>> =
        childSlot(
            source = modalNavigation,
            serializer = null, // т.к. после перезагрузки багуется в вебе (?) //ManageTransactionConfig.serializer(),
            handleBackButton = false,
        ) { config, childCtx ->
            when (config) {
                SettingsModalConfig.CreateAccount -> CreateAccountChild(
                    RealCreateAccountComponent(
                        childCtx, container = {
                            CreateAccountContainer(
                                createAccountUseCase = get(),
                                closeModal = ::dismissSlot
                            )
                        }
                    )
                )

                SettingsModalConfig.CreateCategory -> CreateCategoryChild(
                    RealCreateCategoryComponent(
                        childCtx, container = {
                            CreateCategoryContainer(
                                createCategoryUseCase = get(),
                                closeModal = ::dismissSlot
                            )
                        }
                    )
                )

                is SettingsModalConfig.EditCategory -> EditCategoryChild(
                    RealEditCategoryComponent(
                        childCtx,
                        container = {
                            EditCategoryContainer(
                                config.id,
                                getCategoryByIdUseCase = get(),
                                editCategoryUseCase = get(),
                                deleteCategoryUseCase = get(),
                                closeModal = ::dismissSlot
                            )
                        }
                    )
                )

                is SettingsModalConfig.EditAccount -> EditAccountChild(
                    RealEditAccountComponent(
                        childCtx,
                        container = {
                            EditAccountContainer(
                                config.id,
                                getAccountByIdUseCase = get(),
                                editAccountUseCase = get(),
                                deleteAccountUseCase = get(),
                                closeModal = ::dismissSlot
                            )
                        }
                    )
                )
            }
        }

    override fun dismissSlot() {
        modalNavigation.dismiss()
    }

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
                when (val inst = child.instance) {
                    is AccountsChild -> null
                    is CategoriesChild -> inst.component
                }
            }
        )


    private fun child(config: SettingsConfig, childCtx: ComponentContext): SettingsChild {
        return when (config) {
            SettingsConfig.Accounts -> AccountsChild(
                component = RealAccountsComponent(
                    childCtx, getAccountsFlowUseCase = get(),
                    onCreateClick = {
                        modalNavigation.activate(SettingsModalConfig.CreateAccount)
                    },
                    onItemClick = { id ->
                        modalNavigation.activate(SettingsModalConfig.EditAccount(id))
                    }
                )
            )

            is SettingsConfig.Categories -> CategoriesChild(
                component = RealCategoriesPagesComponent(
                    childCtx,
                    deepLinkUrl = config.deepLinkUrl,
                    getCategoriesFlowUseCase = get(),
                    onCreateClick = {
                        modalNavigation.activate(SettingsModalConfig.CreateCategory)
                    },
                    onItemClick = { id ->
                        modalNavigation.activate(SettingsModalConfig.EditCategory(id))
                    }
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

    override val jsModalSlot: JsValue<JsChildSlot<SettingsModalChild>> by lazy {
        modalSlot.asJsSlot()
    }

    override fun onOutput(output: SettingsOutput) {
        when (output) {
            SettingsOutput.NavigateToAccounts -> nav.select(SettingsConfig.Accounts.index)
            SettingsOutput.NavigateToCategories -> nav.select(SettingsConfig.Categories(null).index)
        }
    }


    override fun subscribeActions(onAction: (SettingsAction) -> Unit): () -> Unit {
        val job = subscribe(scope = componentCoroutineScope) {
            actions.collect { action ->
                onAction(action)
            }
        }
        return { job.cancel() }
    }
}
package settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnResume
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.essenty.dsl.subscribe
import settings.SettingsChild.AccountsChild
import settings.SettingsChild.CategoriesChild
import settings.modals.SettingsModalChild
import settings.modals.SettingsModalConfig
import settings.mvi.SettingsAction
import settings.mvi.SettingsContainer
import settings.mvi.SettingsIntent
import settings.mvi.SettingsState
import settings.sync.RealSyncOverviewComponent
import settings.sync.SyncOverviewComponent
import settings.sync.mvi.SyncOverviewContainer
import utils.Logg
import utils.Url
import utils.interop.*
import utils.path
import utils.presentation.CustomPagesWebNavigation
import utils.presentation.componentCoroutineScope


class RealSettingsComponent(
    componentCtx: ComponentContext,
    container: () -> SettingsContainer,
    deepLinkUrl: Url? = null
) : SettingsComponent, KoinComponent, ComponentContext by componentCtx,
    Store<SettingsState, SettingsIntent, SettingsAction> by componentCtx.retainedStore(factory = container) {

    override val syncOverviewComponent: SyncOverviewComponent = RealSyncOverviewComponent(
        componentCtx.childContext("SyncOverview"),
        container = {
            SyncOverviewContainer(
                logoutUseCase = get(),
                getProfileUseCase = get(),
                syncManager = get(),
                tokenStorage = get()
            )
        },
        onAuthenticateClick = { modalNavigation.activate(SettingsModalConfig.Auth) }
    )

    init {
        lifecycle.doOnResume {
            syncOverviewComponent.updateAuthStatus()
        }

        lifecycle.doOnDestroy {
            Logg.debug { "RealSettingsComponent был полностью уничтожен!" }
        }
    }

    override val jsState: JsValue<SettingsState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }

    val modalNavigation = SlotNavigation<SettingsModalConfig>()


    private val modalSlot: Value<ChildSlot<SettingsModalConfig, SettingsModalChild>> = modalChild()

    override fun dismissSlot() {
        modalNavigation.dismiss()
    }

    override val nav = PagesNavigation<SettingsConfig>()
    private val _pages = childPages(
        source = nav,
        serializer = SettingsConfig.serializer(),
        initialPages = { getInitialPages(deepLinkUrl) },
        childFactory = ::pagesChild,
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
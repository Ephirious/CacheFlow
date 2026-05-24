package settings

import auth.RealAuthComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import editors.accounts.RealCreateAccountComponent
import editors.accounts.RealEditAccountComponent
import editors.accounts.mvi.CreateAccountContainer
import editors.accounts.mvi.EditAccountContainer
import editors.categories.RealCreateCategoryComponent
import editors.categories.RealEditCategoryComponent
import editors.categories.mvi.CreateCategoryContainer
import editors.categories.mvi.EditCategoryContainer
import org.koin.core.component.get
import settings.SettingsChild.AccountsChild
import settings.SettingsChild.CategoriesChild
import settings.modals.SettingsModalChild
import settings.modals.SettingsModalChild.*
import settings.modals.SettingsModalConfig
import settings.pages.accounts.RealAccountsComponent
import settings.pages.categories.RealCategoriesPagesComponent
import utils.Url
import utils.consumePathSegment
import utils.pathSegmentOf

fun RealSettingsComponent.pagesChild(config: SettingsConfig, childCtx: ComponentContext): SettingsChild {
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


fun RealSettingsComponent.modalChild(): Value<ChildSlot<SettingsModalConfig, SettingsModalChild>> =
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

            SettingsModalConfig.Auth -> AuthChild(
                RealAuthComponent(
                    componentCtx = childCtx,
                    backToSettings = {
                        dismissSlot()
                        syncOverviewComponent.updateAuthStatus()
                    }
                )
            )
        }
    }


fun RealSettingsComponent.getInitialPages(deepLinkUrl: Url?): Pages<SettingsConfig> {

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

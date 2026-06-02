package settings

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable
import settings.modals.SettingsModalChild
import settings.mvi.SettingsAction
import settings.mvi.SettingsIntent
import settings.mvi.SettingsState
import settings.pages.SettingsPageComponent
import settings.pages.accounts.AccountsComponent
import settings.pages.categories.CategoriesPagesComponent
import settings.sync.SyncOverviewComponent
import utils.Url
import utils.interop.JsChildSlot
import utils.interop.JsValue
import utils.presentation.DefaultPages
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface SettingsComponent : DefaultPages<SettingsConfig, SettingsChild>, ComponentContext {

    val syncOverviewComponent: SyncOverviewComponent

    val jsModalSlot: JsValue<JsChildSlot<SettingsModalChild>>

    fun onOutput(output: SettingsOutput)

    fun dismissSlot()

    @JsName("state")
    val jsState: JsValue<SettingsState>

    @Suppress("unused")
    fun intent(intent: SettingsIntent)

    fun subscribeActions(onAction: (SettingsAction) -> Unit): () -> Unit
}

@JsExport
sealed class SettingsOutput {
    data object NavigateToCategories : SettingsOutput()
    data object NavigateToAccounts : SettingsOutput()
}

@Serializable
sealed class SettingsConfig(val index: Int) {

    companion object {
        val list: (Categories) -> List<SettingsConfig> = { categories ->
            listOf(categories, Accounts).sortedBy { it.index }
        }
    }

    @Serializable
    data class Categories(val deepLinkUrl: Url?) : SettingsConfig(0)

    @Serializable
    data object Accounts : SettingsConfig(1)
}

@JsExport
sealed class SettingsChild(
    open val component: SettingsPageComponent
) {


    @Suppress("unused")
    class CategoriesChild(override val component: CategoriesPagesComponent) : SettingsChild(component)

    @Suppress("unused")
    class AccountsChild(override val component: AccountsComponent) : SettingsChild(component)
}
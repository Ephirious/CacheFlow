package settings

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable
import settings.pages.accounts.AccountsComponent
import settings.pages.categories.CategoriesPagesComponent
import utils.Url
import utils.presentation.DefaultPages
import kotlin.js.JsExport

@JsExport
interface SettingsComponent : DefaultPages<SettingsConfig, SettingsChild>, ComponentContext {

    fun onOutput(output: SettingsOutput)

//    @JsName("state")
//    val jsState: JsValue<MoreState>

//    @Suppress("unused")
//    fun intent(intent: MoreIntent)
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
sealed class SettingsChild {

    @Suppress("unused")
    class CategoriesChild(val component: CategoriesPagesComponent) : SettingsChild()

    @Suppress("unused")
    class AccountsChild(val component: AccountsComponent) : SettingsChild()
}
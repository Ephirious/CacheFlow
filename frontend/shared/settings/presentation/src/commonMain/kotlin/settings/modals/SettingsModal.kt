package settings.modals

import editors.accounts.CreateAccountComponent
import editors.accounts.EditAccountComponent
import editors.categories.CreateCategoryComponent
import editors.categories.EditCategoryComponent
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
sealed class SettingsModalConfig {
    @Serializable
    data object CreateCategory : SettingsModalConfig()

    @Serializable
    data object CreateAccount : SettingsModalConfig()

    @Serializable
    data class EditCategory(val id: String) : SettingsModalConfig()

    @Serializable
    data class EditAccount(val id: String) : SettingsModalConfig()
}

@JsExport
sealed class SettingsModalChild {

    @Suppress("unused")
    class CreateCategoryChild(val component: CreateCategoryComponent) : SettingsModalChild()

    @Suppress("unused")
    class CreateAccountChild(val component: CreateAccountComponent) : SettingsModalChild()


    @Suppress("unused")
    class EditCategoryChild(val component: EditCategoryComponent) : SettingsModalChild()

    @Suppress("unused")
    class EditAccountChild(val component: EditAccountComponent) : SettingsModalChild()

}
package settings.modals

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
}

@JsExport
sealed class SettingsModalChild {

    @Suppress("unused")
    class CreateCategoryChild(val component: CreateCategoryComponent) : SettingsModalChild()

//    @Suppress("unused")
//    class CreateAccountChild(val component: Any) : SettingsModalChild()


    @Suppress("unused")
    class EditCategoryChild(val component: EditCategoryComponent) : SettingsModalChild()

//    @Suppress("unused")
//    class CreateAccountChild(val component: Any) : SettingsModalChild()
}
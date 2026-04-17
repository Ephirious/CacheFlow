package settings.modals

import editors.categories.CreateCategoryComponent
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
sealed class SettingsModalConfig {
    @Serializable
    data object CreateCategory : SettingsModalConfig()

    @Serializable
    data object CreateAccount : SettingsModalConfig()
}

@JsExport
sealed class SettingsModalChild {

    @Suppress("unused")
    class CreateCategoryChild(val component: CreateCategoryComponent) : SettingsModalChild()

    @Suppress("unused")
    class CreateAccountChild(val component: Any) : SettingsModalChild()
}
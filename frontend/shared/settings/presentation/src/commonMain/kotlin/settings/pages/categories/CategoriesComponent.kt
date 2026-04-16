package settings.pages.categories

import com.arkivanov.decompose.ComponentContext
import settings.pages.SettingsPageComponent
import kotlin.js.JsExport


@JsExport
interface CategoriesComponent : SettingsPageComponent {

}

class RealCategoriesComponent(
    componentCtx: ComponentContext
) : CategoriesComponent, ComponentContext by componentCtx {
    override fun onCreateClick() {
        TODO("Not yet implemented")
    }
}
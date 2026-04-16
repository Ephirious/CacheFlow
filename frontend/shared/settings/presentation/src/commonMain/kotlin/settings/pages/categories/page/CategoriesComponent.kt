package settings.pages.categories.page

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import dbEnums.CategoryType
import editors.models.Category
import utils.interop.JsValue
import utils.interop.asJsValue
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface CategoriesComponent {

    val type: CategoryType

    @JsName("categoriesList")
    val jsCategories: JsValue<List<Category>>
}

class RealCategoriesComponent(
    componentCtx: ComponentContext,
    private val categories: Value<List<Category>>,
    override val type: CategoryType,
) : CategoriesComponent, ComponentContext by componentCtx {
    override val jsCategories: JsValue<List<Category>> by lazy { categories.asJsValue() }
}
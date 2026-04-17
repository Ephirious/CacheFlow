package editors.categories

import com.arkivanov.decompose.ComponentContext
import editors.categories.mvi.EditCategoryContainer
import editors.categories.mvi.EditCategoryIntent
import editors.categories.mvi.EditCategoryState
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface EditCategoryComponent : ComponentContext {

    val type: String
        get() = "edit"

    @JsName("state")
    val jsState: JsValue<EditCategoryState>

    @Suppress("unused")
    fun intent(intent: EditCategoryIntent)
}


class RealEditCategoryComponent(
    componentCtx: ComponentContext,
    container: () -> EditCategoryContainer,
) : EditCategoryComponent, ComponentContext by componentCtx,
    Store<EditCategoryState, EditCategoryIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override val jsState: JsValue<EditCategoryState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}
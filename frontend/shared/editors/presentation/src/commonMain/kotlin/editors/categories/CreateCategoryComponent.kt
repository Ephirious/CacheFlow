package editors.categories

import com.arkivanov.decompose.ComponentContext
import editors.categories.mvi.CreateCategoryContainer
import editors.categories.mvi.CreateCategoryIntent
import editors.categories.mvi.CreateCategoryState
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface CreateCategoryComponent : ComponentContext {

    val type: String
        get() = "create"

    @JsName("state")
    val jsState: JsValue<CreateCategoryState>

    @Suppress("unused")
    fun intent(intent: CreateCategoryIntent)


}


class RealCreateCategoryComponent(
    componentCtx: ComponentContext,
    container: () -> CreateCategoryContainer,
) : CreateCategoryComponent, ComponentContext by componentCtx,
    Store<CreateCategoryState, CreateCategoryIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override val jsState: JsValue<CreateCategoryState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}
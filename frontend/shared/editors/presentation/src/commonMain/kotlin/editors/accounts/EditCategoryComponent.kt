package editors.accounts

import com.arkivanov.decompose.ComponentContext
import editors.accounts.mvi.EditAccountContainer
import editors.accounts.mvi.EditAccountIntent
import editors.accounts.mvi.EditAccountState
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface EditAccountComponent : ComponentContext {

    val type: String
        get() = "edit"

    @JsName("state")
    val jsState: JsValue<EditAccountState>

    @Suppress("unused")
    fun intent(intent: EditAccountIntent)
}


class RealEditAccountComponent(
    componentCtx: ComponentContext,
    container: () -> EditAccountContainer,
) : EditAccountComponent, ComponentContext by componentCtx,
    Store<EditAccountState, EditAccountIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override val jsState: JsValue<EditAccountState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}
package editors.accounts

import com.arkivanov.decompose.ComponentContext
import editors.accounts.mvi.CreateAccountContainer
import editors.accounts.mvi.CreateAccountIntent
import editors.accounts.mvi.CreateAccountState
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface CreateAccountComponent : ComponentContext {

    val type: String
        get() = "create"

    @JsName("state")
    val jsState: JsValue<CreateAccountState>

    @Suppress("unused")
    fun intent(intent: CreateAccountIntent)


}


class RealCreateAccountComponent(
    componentCtx: ComponentContext,
    container: () -> CreateAccountContainer,
) : CreateAccountComponent, ComponentContext by componentCtx,
    Store<CreateAccountState, CreateAccountIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override val jsState: JsValue<CreateAccountState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}
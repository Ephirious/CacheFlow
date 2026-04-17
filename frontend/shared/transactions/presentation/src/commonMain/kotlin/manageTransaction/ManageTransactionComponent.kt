package manageTransaction

import com.arkivanov.decompose.ComponentContext
import manageTransaction.mvi.ManageTransactionContainer
import manageTransaction.mvi.ManageTransactionIntent
import manageTransaction.mvi.ManageTransactionState
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface ManageTransactionComponent : ComponentContext {

    @JsName("state")
    val jsState: JsValue<ManageTransactionState>

    @Suppress("unused")
    fun intent(intent: ManageTransactionIntent)


}


class RealManageTransactionComponent(
    componentCtx: ComponentContext,
    container: () -> ManageTransactionContainer,
) : ManageTransactionComponent, ComponentContext by componentCtx,
    Store<ManageTransactionState, ManageTransactionIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override val jsState: JsValue<ManageTransactionState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}
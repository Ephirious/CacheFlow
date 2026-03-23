package transactions

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import transactions.mvi.TransactionsContainer
import transactions.mvi.TransactionsIntent
import transactions.mvi.TransactionsState
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface TransactionsComponent : ComponentContext {

    @JsName("state")
    val jsState: JsValue<TransactionsState>

    @Suppress("unused")
    fun intent(intent: TransactionsIntent)
}


class RealTransactionsComponent(
    componentCtx: ComponentContext,
    container: () -> TransactionsContainer,
) : TransactionsComponent, ComponentContext by componentCtx,
    Store<TransactionsState, TransactionsIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override val jsState: JsValue<TransactionsState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}
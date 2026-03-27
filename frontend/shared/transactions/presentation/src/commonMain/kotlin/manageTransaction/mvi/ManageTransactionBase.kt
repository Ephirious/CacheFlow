package manageTransaction.mvi

import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.plugin
import utils.types.BigDecimal
import utils.annotations.DataCopyable
import kotlin.js.JsExport


@JsExport
@DataCopyable
interface ManageTransactionFormBaseState : MVIState {
    val value: BigDecimal
    val transactionType: Any
    val category: Any

    //    val affectedAccountId: String?,
//    val date
    val note: String
}


@JsExport
sealed class ManageTransactionBaseIntent : MVIIntent {

    sealed interface Internal

    data class ChangedValue(val value: BigDecimal) : ManageTransactionBaseIntent(), Internal
    data class ChangedNote(val note: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedType(val type: Any) : ManageTransactionBaseIntent(), Internal
}

@FlowMVIDSL
fun <S : MVIState, I : ManageTransactionBaseIntent, A : MVIAction> manageTransactionBasePlugin() =
    plugin<S, I, A> {
        name = "ManageTransactionBasePlugin"

        onIntent { intent ->
            val baseIntent = intent as? ManageTransactionBaseIntent.Internal ?: return@onIntent intent

            updateState {
                if (this@updateState !is ManageTransactionFormBaseState) return@updateState this@updateState
                when (baseIntent) {
                    is ManageTransactionBaseIntent.ChangedValue -> copyBase(value = baseIntent.value)
                    is ManageTransactionBaseIntent.ChangedNote -> copyBase(note = baseIntent.note)
                    is ManageTransactionBaseIntent.ChangedType -> copyBase(transactionType = baseIntent.type)
                }
            }
            null
        }
    }
package manageTransaction.mvi

import pro.respawn.flowmvi.api.MVIState
import utils.BigDecimal
import utils.annotations.DataCopyableNode
import kotlin.js.JsExport

@JsExport
sealed class ManageTransactionState : MVIState {

    @DataCopyableNode
    data class OK(val form: FormState, val isCreateMode: Boolean) : ManageTransactionState(),
        ManageTransactionFormBaseState by form {

        @DataCopyableNode
        data class FormState(
            override val value: BigDecimal,
            override val transactionType: Any,
            override val category: Any,
            override val note: String
        ) : ManageTransactionFormBaseState
    }

    data class FatalError(val message: String) : ManageTransactionState()

}


@JsExport
sealed class ManageTransactionIntent : ManageTransactionBaseIntent() {
    data object Save : ManageTransactionIntent()
    data object Delete : ManageTransactionIntent()
}
package manageTransaction.mvi

import editors.models.Account
import editors.models.Category
import kotlinx.datetime.LocalDate
import pro.respawn.flowmvi.api.MVIState
import utils.types.BigDecimal
import utils.annotations.DataCopyableNode
import kotlin.js.JsExport

@JsExport
sealed class ManageTransactionState : MVIState {

    @DataCopyableNode
    data class OK(val form: FormState, val isCreateMode: Boolean) : ManageTransactionState() {

        @DataCopyableNode
        data class FormState(
            override val value: BigDecimal,
            override val transactionType: ManageTransactionType,
            override val note: String,
            override val categories: List<Category>,
            override val accounts: List<Account>,
            override val date: LocalDate
        ) : ManageTransactionFormBaseState
    }

    data class FatalError(val message: String) : ManageTransactionState()

}


@JsExport
sealed class ManageTransactionIntent : ManageTransactionBaseIntent() {
    data object ClickedSave : ManageTransactionIntent()
    data object ClickedDelete : ManageTransactionIntent()
}
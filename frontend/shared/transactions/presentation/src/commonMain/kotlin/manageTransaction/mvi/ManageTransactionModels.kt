package manageTransaction.mvi

import editors.models.Account
import editors.models.Category
import kotlinx.datetime.LocalDate
import manageTransaction.mvi.ManageTransactionType.Outcome
import pro.respawn.flowmvi.api.MVIState
import utils.annotations.DataCopyableNode
import utils.toLocalDate
import kotlin.js.JsExport
import kotlin.time.Clock

@JsExport
sealed class ManageTransactionState : MVIState {

    @DataCopyableNode
    data class OK(val form: FormState, val isCreateMode: Boolean) : ManageTransactionState() {

        @DataCopyableNode
        data class FormState(
            override val value: String = "",
            override val transactionType: ManageTransactionType = Outcome(
                categoryId = null,
                accountId = null
            ),
            override val note: String = "",
            override val categories: List<Category> = emptyList(),
            override val accounts: List<Account> = emptyList(),
            override val date: LocalDate = Clock.System.now().toLocalDate(),
            override val validation: ManageTransactionFormBaseStateValidationErrors = ManageTransactionFormBaseStateValidationErrors()
        ) : ManageTransactionFormBaseState<ManageTransactionFormBaseStateValidationErrors>
    }

    data class FatalError(val message: String) : ManageTransactionState()

}


@JsExport
sealed class ManageTransactionIntent : ManageTransactionBaseIntent() {
    data object ClickedSave : ManageTransactionIntent()
    data object ClickedDelete : ManageTransactionIntent()
}
package manageTransaction.mvi

import dbEnums.CategoryType
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
            override val incomeCategories: List<Category> = emptyList(),
            override val outcomeCategories: List<Category> = emptyList(),
            override val accounts: List<Account> = emptyList(),
            override val date: LocalDate = Clock.System.now().toLocalDate(),
            override val validation: ManageTransactionFormBaseValidationErrors = ManageTransactionFormBaseValidationErrors()
        ) : ManageTransactionFormBaseState<ManageTransactionFormBaseValidationErrors> {
            fun findAccount(id: String?) = accounts.firstOrNull { it.id == id }
            fun findCategory(id: String?, type: CategoryType): Category? {
                val categories = if (type == CategoryType.INCOME) incomeCategories else outcomeCategories
                return categories.firstOrNull { it.id == id }
            }

            val isTransfer get() = transactionType is ManageTransactionType.Transfer
        }
    }

    data class FatalError(val message: String, val lastValidForm: OK.FormState?) : ManageTransactionState()

}


@JsExport
sealed class ManageTransactionIntent : ManageTransactionBaseIntent() {
    data object ClickedSave : ManageTransactionIntent()
    data object ClickedDelete : ManageTransactionIntent()

    data object ClickedTryAgain : ManageTransactionIntent()
}
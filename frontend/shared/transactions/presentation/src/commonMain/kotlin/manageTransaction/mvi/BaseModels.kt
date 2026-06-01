package manageTransaction.mvi

import core_validation.GenerateValidator
import core_validation.data.IdValidator
import core_validation.data.transaction.TransactionNoteValidator
import core_validation.data.transaction.TransactionValueValidator
import core_validation.data.transaction.TransferAccountIdValidator
import core_validation.data.transaction.internal.TransferAccountsContext
import editors.models.Account
import editors.models.Category
import kotlinx.datetime.LocalDate
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import utils.annotations.DataCopyable
import utils.annotations.DataCopyableNode
import kotlin.js.JsExport

@JsExport
@DataCopyable
@GenerateValidator
interface ManageTransactionFormBaseState<V> : MVIState {

    @TransactionValueValidator
    val value: String
    val transactionType: ManageTransactionType

    val date: LocalDate

    @TransactionNoteValidator
    val note: String

    val incomeCategories: List<Category>
    val outcomeCategories: List<Category>
    val accounts: List<Account>
    val validation: V
}

@JsExport
sealed class ManageTransactionType(
    @Suppress("unused")
    // used for TS
    val type: String,
) {

    @DataCopyable
    @GenerateValidator
    sealed interface IncomeOrOutcome<V> {
        @IdValidator
        val categoryId: String?

        @IdValidator
        val accountId: String?

        val validation: V
    }

    @DataCopyableNode
    data class Income(
        override val categoryId: String?,
        override val accountId: String?,
        override val validation: IncomeOrOutcomeValidationErrors = IncomeOrOutcomeValidationErrors()
    ) : ManageTransactionType("Income"), IncomeOrOutcome<IncomeOrOutcomeValidationErrors>

    @DataCopyableNode
    data class Outcome(
        override val categoryId: String?,
        override val accountId: String?,
        override val validation: IncomeOrOutcomeValidationErrors = IncomeOrOutcomeValidationErrors()
    ) : ManageTransactionType("Outcome"), IncomeOrOutcome<IncomeOrOutcomeValidationErrors>

    @GenerateValidator
    data class Transfer(
        @TransferAccountIdValidator
        override val fromId: String?,

        @TransferAccountIdValidator
        override val toId: String?,
        val validation: TransferValidationErrors = TransferValidationErrors()
    ) : ManageTransactionType("Transfer"), TransferAccountsContext
}


@JsExport
sealed class ManageTransactionBaseIntent : MVIIntent {

    sealed interface Internal

    data class ChangedValue(val value: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedNote(val note: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedType(val typeClass: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedAccount(val accountId: String) : ManageTransactionBaseIntent(), Internal

    data class ChangedTransferToAccount(val accountId: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedCategory(val categoryId: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedDate(val date: String) : ManageTransactionBaseIntent(), Internal
}
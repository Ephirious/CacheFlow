package manageTransaction.mvi

import editors.models.Account
import editors.models.Category
import kotlinx.datetime.LocalDate
import manageTransaction.validation.DiffTransferAccounts
import manageTransaction.validation.StringAmount
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import utils.annotations.DataCopyable
import utils.annotations.DataCopyableNode
import utils.annotations.GenerateValidator
import utils.annotations.validation.MaxLen
import utils.annotations.validation.NotEmptyOrNullString
import kotlin.js.JsExport

@JsExport
@DataCopyable
@GenerateValidator
interface ManageTransactionFormBaseState<V> : MVIState {

    @StringAmount
    @MaxLen(10)
    val value: String
    val transactionType: ManageTransactionType

    val date: LocalDate
    val note: String

    val categories: List<Category>
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
        @NotEmptyOrNullString
        val categoryId: String?

        @NotEmptyOrNullString
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
        @NotEmptyOrNullString
        @DiffTransferAccounts
        val fromId: String?,

        @NotEmptyOrNullString
        @DiffTransferAccounts
        val toId: String?,
        val validation: TransferValidationErrors = TransferValidationErrors()
    ) : ManageTransactionType("Transfer")
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
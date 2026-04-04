package manageTransaction.mvi

import editors.models.Account
import editors.models.Category
import kotlinx.datetime.LocalDate
import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.plugin
import utils.annotations.DataCopyable
import utils.annotations.DataCopyableNode
import utils.types.BigDecimal
import kotlin.js.JsExport


@JsExport
@DataCopyable
interface ManageTransactionFormBaseState : MVIState {
    val value: BigDecimal
    val transactionType: ManageTransactionType

    val categories: List<Category>
    val accounts: List<Account>
    val date: LocalDate
    val note: String
}

@JsExport
sealed class ManageTransactionType(
    @Suppress("unused")
    // used for TS
    val type: String
) {
    @JsExport.Ignore
    @DataCopyable
    sealed interface IncomeOrOutcome {
        val categoryId: String?
        val accountId: String?
    }

    @DataCopyableNode
    data class Income(
        override val categoryId: String?,
        override val accountId: String?
    ) : ManageTransactionType("Income"), IncomeOrOutcome

    @DataCopyableNode
    data class Outcome(
        override val categoryId: String?, override val accountId: String?
    ) : ManageTransactionType("Outcome"), IncomeOrOutcome

    data class Transfer(
        val fromId: String?,
        val toId: String?
    ) : ManageTransactionType("Transfer")
}


@JsExport
sealed class ManageTransactionBaseIntent : MVIIntent {

    sealed interface Internal

    data class ChangedValue(val value: BigDecimal) : ManageTransactionBaseIntent(), Internal
    data class ChangedNote(val note: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedType(val typeClass: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedAccount(val accountId: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedCategory(val categoryId: String) : ManageTransactionBaseIntent(), Internal
    data class ChangedDate(val date: String) : ManageTransactionBaseIntent(), Internal
}

@FlowMVIDSL
fun <S : MVIState, I : ManageTransactionBaseIntent, A : MVIAction, F : ManageTransactionFormBaseState>
        manageTransactionBasePlugin(
    getForm: S.() -> F?,
    setForm: S.(F) -> S
) =
    plugin<S, I, A> {
        name = "ManageTransactionBasePlugin"
        onIntent { intent ->
            val baseIntent = intent as? ManageTransactionBaseIntent.Internal ?: return@onIntent intent
            updateState {
                val currentForm = getForm() ?: return@updateState this

                val updatedForm = with(currentForm) {
                    when (baseIntent) {
                        is ManageTransactionBaseIntent.ChangedValue -> copyBase(value = baseIntent.value)
                        is ManageTransactionBaseIntent.ChangedNote -> copyBase(note = baseIntent.note)
                        is ManageTransactionBaseIntent.ChangedDate -> {
                            val datePart = baseIntent.date.substringBefore('T')
                            copyBase(date = LocalDate.parse(datePart))
                        }

                        is ManageTransactionBaseIntent.ChangedType -> {
                            copyBase(transactionType = transactionType.changeType(baseIntent.typeClass))
                        }

                        is ManageTransactionBaseIntent.ChangedAccount -> {
                            copyBase(transactionType = transactionType.updateAccount(baseIntent.accountId))
                        }

                        is ManageTransactionBaseIntent.ChangedCategory -> {
                            copyBase(transactionType = transactionType.updateCategory(baseIntent.categoryId))
                        }
                    }
                }
                setForm(updatedForm)
            }

            null
        }
    }

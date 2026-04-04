package manageTransaction.mvi

import editors.models.Account
import editors.models.Category
import kotlinx.datetime.LocalDate
import manageTransaction.mvi.ManageTransactionType.*
import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.plugin
import utils.annotations.DataCopyable
import utils.types.BigDecimal
import kotlin.js.JsExport

@JsExport
sealed class ManageTransactionType(
    val type: String
) {
    data class Income(
        val categoryId: String?,
        val accountId: String?,
    ) : ManageTransactionType("Income")

    data class Outcome(
        val categoryId: String?,
        val accountId: String?,
    ) : ManageTransactionType("Outcome")

    data class Transfer(
        val fromId: String?,
        val toId: String?
    ) : ManageTransactionType("Transfer")


}

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
fun <S : MVIState, I : ManageTransactionBaseIntent, A : MVIAction> manageTransactionBasePlugin() =
    plugin<S, I, A> {
        name = "ManageTransactionBasePlugin"

        onIntent { intent ->
            val baseIntent = intent as? ManageTransactionBaseIntent.Internal ?: return@onIntent intent

            updateState {

                val okState = this as? ManageTransactionState.OK
                    ?: return@updateState this

                val newForm = when (baseIntent) {

                    is ManageTransactionBaseIntent.ChangedValue ->
                        okState.form.copy(value = baseIntent.value)

                    is ManageTransactionBaseIntent.ChangedNote ->
                        okState.form.copy(note = baseIntent.note)

                    is ManageTransactionBaseIntent.ChangedType -> {

                        val (accountId, categoryId) =
                            when (val type = okState.form.transactionType) {
                                is Income -> type.accountId to type.categoryId
                                is Outcome -> type.accountId to type.categoryId
                                is Transfer -> type.fromId to null
                            }

                        okState.form.copy(
                            transactionType =
                                when (baseIntent.typeClass) {
                                    "Income" ->
                                        Income(categoryId, accountId)

                                    "Outcome" ->
                                        Outcome(categoryId, accountId)

                                    else ->
                                        Transfer(accountId, null)
                                }
                        )
                    }

                    is ManageTransactionBaseIntent.ChangedAccount -> {
                        if (okState.form.transactionType is Income) {
                            okState.form.copy(transactionType = okState.form.transactionType.copy(accountId = baseIntent.accountId))
                        } else if (okState.form.transactionType is Outcome) {
                            okState.form.copy(transactionType = okState.form.transactionType.copy(accountId = baseIntent.accountId))
                        } else {
                            okState.form
                        }
                    }

                    is ManageTransactionBaseIntent.ChangedCategory -> {
                        when (okState.form.transactionType) {
                            is Income -> {
                                okState.form.copy(transactionType = okState.form.transactionType.copy(categoryId = baseIntent.categoryId))
                            }

                            is Outcome -> {
                                okState.form.copy(transactionType = okState.form.transactionType.copy(categoryId = baseIntent.categoryId))
                            }

                            else -> {
                                okState.form
                            }
                        }
                    }

                    is ManageTransactionBaseIntent.ChangedDate -> {
                        okState.form.copy(date = LocalDate.parse(baseIntent.date.split("T")[0]))
                    }
                }

                okState.copy(form = newForm) as S
            }

            null
        }
    }
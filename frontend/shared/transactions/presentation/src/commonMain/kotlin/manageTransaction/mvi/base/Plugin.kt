package manageTransaction.mvi.base

import kotlinx.datetime.LocalDate
import manageTransaction.mvi.*
import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.plugin

@FlowMVIDSL
fun <S : MVIState, I : ManageTransactionBaseIntent, A : MVIAction, F : ManageTransactionFormBaseState<ManageTransactionFormBaseStateValidationErrors>>
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
                        is ManageTransactionBaseIntent.ChangedValue -> {
                            copyBase(
                                value = baseIntent.value
                            ).validated(ManageTransactionFormBaseStateValidationFields.value)
                        }

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
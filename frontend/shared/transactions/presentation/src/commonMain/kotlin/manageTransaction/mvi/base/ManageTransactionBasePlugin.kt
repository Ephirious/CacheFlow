package manageTransaction.mvi.base

import kotlinx.datetime.LocalDate
import manageTransaction.mvi.*
import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.plugin
import utils.presentation.flowMVI.customOnIntent

@FlowMVIDSL
fun <S : MVIState, I : ManageTransactionBaseIntent, A : MVIAction, F : ManageTransactionFormBaseState<ManageTransactionFormBaseValidationErrors>>
        manageTransactionBasePlugin(
    getForm: S.() -> F?,
    setForm: S.(F) -> S
) =
    plugin<S, I, A> {
        name = "ManageTransactionBasePlugin"
        customOnIntent { intent ->
            val baseIntent = intent as? ManageTransactionBaseIntent.Internal ?: return@customOnIntent intent
            updateState {
                val currentForm = getForm() ?: return@updateState this

                @Suppress("UNCHECKED_CAST")
                val updatedForm = with(currentForm) {
                    when (baseIntent) {
                        is ManageTransactionBaseIntent.ChangedValue -> {
                            copyBase(
                                value = baseIntent.value.trim()
                            ).validated(ManageTransactionFormBaseValidationFields.value)
                        }

                        is ManageTransactionBaseIntent.ChangedNote -> copyBase(note = baseIntent.note.trim())

                        is ManageTransactionBaseIntent.ChangedDate -> {
                            val datePart = baseIntent.date.substringBefore('T')
                            copyBase(date = LocalDate.parse(datePart))
                        }

                        is ManageTransactionBaseIntent.ChangedType -> {
                            copyBase(
                                transactionType = transactionType.changeType(baseIntent.typeClass)
                                    .validated()
                            )
                        }


                        is ManageTransactionBaseIntent.ChangedCategory -> {
                            copyBase(
                                transactionType = transactionType.updateCategory(baseIntent.categoryId).validated(
                                    IncomeOrOutcomeValidationFields.categoryId
                                )
                            )
                        }

                        is ManageTransactionBaseIntent.ChangedAccount -> {
                            copyBase(
                                transactionType = transactionType.updateAccount(baseIntent.accountId).validated(
                                    IncomeOrOutcomeValidationFields.categoryId)
                            )
                        }


                        is ManageTransactionBaseIntent.ChangedTransferToAccount -> {
                            copyBase(
                                transactionType =
                                    transactionType.updateTransferToAccount(baseIntent.accountId)
                                        .validated()
                            )
                        }
                    } as F
                }
                setForm(updatedForm)
            }

            null
        }
    }
package manageTransaction.mvi

import manageTransaction.mvi.base.validated
import manageTransaction.mvi.base.validationHasErrors

fun ManageTransactionContainer.getInitial(
    form: ManageTransactionState.OK.FormState? = null
) =
    ManageTransactionState.OK(
        form = form ?: ManageTransactionState.OK.FormState(),
        isCreateMode = isCreateMode,
    ).allValidate()

fun ManageTransactionState.OK.allValidate(
) = copy(form = form.let {
    it.copy(validation = it.validate(), transactionType = it.transactionType.validated())
})

fun ManageTransactionState.OK.allValidated(
) = allValidate().isValid()

fun ManageTransactionState.OK.isValid(
) = !form.validation.hasErrors && !form.transactionType.validationHasErrors()
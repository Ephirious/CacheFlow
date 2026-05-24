package editors.accounts.mvi

import utils.types.HexColor


fun EditAccountContainer.getInitial(
    form: EditAccountFormState? = null
) =
    EditAccountState.OK(
        form = form ?: EditAccountFormState(
            title = "",
            color = HexColor("#FF0000"),
            validation = ManageAccountFormBaseValidationErrors()
        ),
    ).allValidate()

fun EditAccountState.OK.allValidate(
) = copy(form = form.let {
    it.copy(validation = it.validate())
})

fun EditAccountState.OK.allValidated(
) = allValidate().isValid()

fun EditAccountState.OK.isValid(
) = !form.validation.hasErrors
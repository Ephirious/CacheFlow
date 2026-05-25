package editors.accounts.mvi

import utils.types.HexColor


fun CreateAccountContainer.getInitial(
    form: CreateAccountFormState? = null
) =
    CreateAccountState.OK(
        form = form ?: CreateAccountFormState(
            initialBalance = "0",
            title = "",
            color = HexColor("#4A86F7"),
            validation = CreateAccountFormValidationErrors()
        ),
    ).allValidate()

fun CreateAccountState.OK.allValidate(
) = copy(form = form.let {
    it.copy(validation = it.validate())
})

fun CreateAccountState.OK.allValidated(
) = allValidate().isValid()

fun CreateAccountState.OK.isValid(
) = !form.validation.hasErrors
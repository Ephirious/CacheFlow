package auth.registration.mvi


val RegistrationStep.fieldsToValidate: List<RegistrationValidationFields>
    get() = when (this) {
        RegistrationStep.InputDetails -> listOf(
            RegistrationValidationFields.nameInput,
            RegistrationValidationFields.emailInput,
            RegistrationValidationFields.passwordInput
        )

        else -> listOf(
            RegistrationValidationFields.nameInput,
            RegistrationValidationFields.emailInput,
            RegistrationValidationFields.passwordInput,
            RegistrationValidationFields.codeInput
        )
    }


fun RegistrationState.validatedForCurrentStep(): RegistrationState {
    val cleanState = this.copy(validation = RegistrationValidationErrors())

    return step.fieldsToValidate.fold(cleanState) { state, field ->
        state.validated(field)
    }
}

fun RegistrationState.isAllValidated() =
    !validatedForCurrentStep().validation.hasErrors
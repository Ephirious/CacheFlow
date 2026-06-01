package auth.login.mvi

fun LoginState.isAllValidated(
) = !validated().validation.hasErrors

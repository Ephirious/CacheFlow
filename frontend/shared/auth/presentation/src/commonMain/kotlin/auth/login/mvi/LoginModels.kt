package auth.login.mvi

import core_validation.GenerateValidator
import core_validation.data.auth.AuthEmailValidator
import core_validation.data.auth.AuthPasswordValidator
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import kotlin.js.JsExport

@JsExport
@GenerateValidator
data class LoginState(
    @AuthEmailValidator
    val emailInput: String = "",
    @AuthPasswordValidator
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val validation: LoginValidationErrors = LoginValidationErrors()
) : MVIState {
    companion object {
        fun getInitial() = LoginState().validated()
    }
}

@JsExport
sealed class LoginIntent : MVIIntent {
    data class ChangeEmail(val email: String) : LoginIntent()
    data class ChangePassword(val password: String) : LoginIntent()

    data object SubmitClicked : LoginIntent()
    data object BackClicked : LoginIntent()
}

@JsExport
sealed class LoginAction : MVIAction {
    data class Error(val msg: String) : LoginAction()
}
package auth.login.mvi

import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import kotlin.js.JsExport

@JsExport
data class LoginState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false
) : MVIState {
    companion object {
        fun getInitial() = LoginState()
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
sealed class LoginAction: MVIAction {
    data class Error(val msg: String) : LoginAction()
}
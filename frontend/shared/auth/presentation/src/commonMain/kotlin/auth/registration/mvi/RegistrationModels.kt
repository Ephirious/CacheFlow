package auth.registration.mvi

import auth.UserId
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import kotlin.js.JsExport


@JsExport
data class RegistrationState(
    val step: RegistrationStep = RegistrationStep.InputDetails,
    val isLoading: Boolean = false,

    val nameInput: String = "",
    val emailInput: String = "",
    val passwordInput: String = "",
    val codeInput: String = "",

    // hidden from user
    val savedUserId: UserId? = null
) : MVIState {

    companion object {
        fun getInitial() = RegistrationState()
    }
}

@JsExport
enum class RegistrationStep {
    InputDetails,
    EnterCode
}


@JsExport
sealed class RegistrationIntent : MVIIntent {
    data class ChangeName(val name: String) : RegistrationIntent()
    data class ChangeEmail(val email: String) : RegistrationIntent()
    data class ChangePassword(val password: String) : RegistrationIntent()
    data class ChangeCode(val code: String) : RegistrationIntent()


    data object SubmitRegistrationClicked : RegistrationIntent()
    data object SubmitCodeClicked : RegistrationIntent()
    data object ResendCodeClicked : RegistrationIntent()
    data object BackClicked : RegistrationIntent()
}


@JsExport
sealed class RegistrationAction : MVIAction {
    data object CodeSent : RegistrationAction()
    data class Error(val msg: String) : RegistrationAction()
}
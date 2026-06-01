package auth.registration.mvi

import auth.UserId
import core_validation.GenerateValidator
import core_validation.data.auth.AuthEmailValidator
import core_validation.data.auth.AuthOTPCodeValidator
import core_validation.data.auth.AuthPasswordValidator
import core_validation.data.auth.AuthUsernameValidator
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import kotlin.js.JsExport


@JsExport
@GenerateValidator
data class RegistrationState(
    val step: RegistrationStep = RegistrationStep.InputDetails,
    val isLoading: Boolean = false,

    @AuthUsernameValidator
    val nameInput: String = "",
    @AuthEmailValidator
    val emailInput: String = "",
    @AuthPasswordValidator
    val passwordInput: String = "",

    @AuthOTPCodeValidator
    val codeInput: String = "",

    // hidden from user
    val savedUserId: UserId? = null,
    val validation: RegistrationValidationErrors = RegistrationValidationErrors()
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
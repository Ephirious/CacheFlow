package auth

import auth.registration.RegistrationComponent
import com.arkivanov.decompose.ComponentContext
import kotlin.js.JsExport

@JsExport
interface AuthComponent : ComponentContext {

    val backToSettings: () -> Unit

    fun onOutput(output: AuthOutput)

    val registrationComponent: RegistrationComponent

}

@JsExport
sealed class AuthOutput {
    data object NavigateToRegistration : AuthOutput()
    data object NavigateToLogin : AuthOutput()
}


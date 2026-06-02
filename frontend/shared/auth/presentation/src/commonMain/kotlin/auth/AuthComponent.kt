package auth

import auth.login.LoginComponent
import auth.registration.RegistrationComponent
import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable
import utils.presentation.DefaultPages
import kotlin.js.JsExport

@JsExport
interface AuthComponent : DefaultPages<AuthConfig, AuthChild>, ComponentContext {

    val backToSettings: () -> Unit

    fun onOutput(output: AuthOutput)
}

@JsExport
sealed class AuthOutput {
    data object NavigateToRegistration : AuthOutput()
    data object NavigateToLogin : AuthOutput()
}

@Serializable
sealed class AuthConfig(val index: Int) {

    companion object {
        val list: () -> List<AuthConfig> = {
            listOf(Registration, Login).sortedBy { it.index }
        }
    }

    @Serializable
    data object Registration : AuthConfig(0)

    @Serializable
    data object Login : AuthConfig(1)
}

@JsExport
sealed class AuthChild {

    @Suppress("unused")
    data class RegistrationChild(val component: RegistrationComponent) : AuthChild()

    @Suppress("unused")
    data class LoginChild(val component: LoginComponent) : AuthChild()
}


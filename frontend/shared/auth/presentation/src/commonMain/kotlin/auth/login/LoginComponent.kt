package auth.login

import auth.login.mvi.LoginAction
import auth.login.mvi.LoginIntent
import auth.login.mvi.LoginState
import com.arkivanov.decompose.ComponentContext
import utils.interop.JsValue
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface LoginComponent : ComponentContext {


    @JsName("state")
    val jsState: JsValue<LoginState>

    @Suppress("unused")
    fun intent(intent: LoginIntent)

    fun subscribeActions(onAction: (LoginAction) -> Unit): () -> Unit
}


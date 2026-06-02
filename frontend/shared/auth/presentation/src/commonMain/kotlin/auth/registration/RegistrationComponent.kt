package auth.registration

import auth.registration.mvi.RegistrationAction
import auth.registration.mvi.RegistrationIntent
import auth.registration.mvi.RegistrationState
import com.arkivanov.decompose.ComponentContext
import utils.interop.JsValue
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface RegistrationComponent : ComponentContext {


    @JsName("state")
    val jsState: JsValue<RegistrationState>

    @Suppress("unused")
    fun intent(intent: RegistrationIntent)

    fun subscribeActions(onAction: (RegistrationAction) -> Unit): () -> Unit
}
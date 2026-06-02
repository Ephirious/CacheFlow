package auth.login

import auth.login.mvi.LoginAction
import auth.login.mvi.LoginContainer
import auth.login.mvi.LoginIntent
import auth.login.mvi.LoginState
import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinComponent
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.essenty.dsl.subscribe
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope

class RealLoginComponent(
    componentCtx: ComponentContext,
    container: () -> LoginContainer
) : LoginComponent, KoinComponent, ComponentContext by componentCtx,
    Store<LoginState, LoginIntent, LoginAction> by componentCtx.retainedStore(factory = container) {


    override val jsState: JsValue<LoginState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }


    override fun subscribeActions(onAction: (LoginAction) -> Unit): () -> Unit {
        val job = subscribe(scope = componentCoroutineScope) {
            actions.collect { action ->
                onAction(action)
            }
        }
        return { job.cancel() }
    }
}

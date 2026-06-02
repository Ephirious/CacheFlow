package auth.registration

import auth.registration.mvi.RegistrationAction
import auth.registration.mvi.RegistrationContainer
import auth.registration.mvi.RegistrationIntent
import auth.registration.mvi.RegistrationState
import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinComponent
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.essenty.dsl.subscribe
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope

class RealRegistrationComponent(
    componentCtx: ComponentContext,
    container: () -> RegistrationContainer
) : RegistrationComponent, KoinComponent, ComponentContext by componentCtx,
    Store<RegistrationState, RegistrationIntent, RegistrationAction> by componentCtx.retainedStore(factory = container) {


    override val jsState: JsValue<RegistrationState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }


    override fun subscribeActions(onAction: (RegistrationAction) -> Unit): () -> Unit {
        val job = subscribe(scope = componentCoroutineScope) {
            actions.collect { action ->
                onAction(action)
            }
        }
        return { job.cancel() }
    }
}

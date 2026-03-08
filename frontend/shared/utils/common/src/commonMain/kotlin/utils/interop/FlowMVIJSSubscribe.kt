package utils.interop

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import pro.respawn.flowmvi.annotation.InternalFlowMVIAPI
import pro.respawn.flowmvi.api.*
import pro.respawn.flowmvi.dsl.state
import pro.respawn.flowmvi.essenty.dsl.subscribe

@OptIn(DelicateStoreApi::class, InternalFlowMVIAPI::class)
fun <S : MVIState, I : MVIIntent, A : MVIAction> Store<S, I, A>.jsStateSubscribe(
    lifecycleOwner: LifecycleOwner,
    scope: CoroutineScope,
    mode: SubscriptionMode = SubscriptionMode.Started,
): JsValueImpl<S> {
    val valueRelay = MutableValue(state)

    with(lifecycleOwner) {
        subscribe(store = this@jsStateSubscribe, scope = scope, mode = mode) {
            this.states.collect {
                valueRelay.value = it
            }
        }
    }
    return JsValueImpl(valueRelay)
}
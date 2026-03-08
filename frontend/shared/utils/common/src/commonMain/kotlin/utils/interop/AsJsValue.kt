package utils.interop

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.coroutines.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.ImmutableContainer
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.api.Provider
import pro.respawn.flowmvi.api.SubscriptionMode
import pro.respawn.flowmvi.dsl.collect
import pro.respawn.flowmvi.dsl.state
import pro.respawn.flowmvi.essenty.lifecycle.asEssentyLifecycle

fun <T : Any> Value<T>.asJsValue(): JsValue<T> = JsValueImpl(this)

fun <T : Any> StateFlow<T>.asJsValue(
    scope: CoroutineScope
): JsValue<T> {
    val valueRelay = MutableValue(this.value)




    scope.launch {
        collect { newValue ->
            valueRelay.value = newValue
        }
    }

    return JsValueImpl(valueRelay)
}
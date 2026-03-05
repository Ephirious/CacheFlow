package utils.interop

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

fun <T : Any> CoroutineScope.createJsValue(initialValue: T): Pair<JsValue<T>, (T) -> Unit> {
    val valueRelay = MutableValue(initialValue)
    val jsValue = JsValueImpl(valueRelay)

    val update = { newValue: T ->
        valueRelay.value = newValue
    }

    return jsValue to update
}
package utils.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@JsExport
interface JsDisposable {
    fun dispose()
}

@JsExport
interface JsValue<T : Any> {
    val value: T
    fun subscribe(observer: (T) -> Unit): JsDisposable
}

class JsValueImpl<T : Any>(private val origin: Value<T>) : JsValue<T> {
    override val value: T get() = origin.value

    override fun subscribe(observer: (T) -> Unit): JsDisposable {
        val cancellation = origin.subscribe(observer)
        return object : JsDisposable {
            override fun dispose() = cancellation.cancel()
        }
    }
}

fun <T : Any> Value<T>.asJsValue(): JsValue<T> = JsValueImpl(this)


@JsExport
interface JsChildStack<out T : Any> {
    val active: T
    val backStack: Array<out T>
}

class JsChildStackImpl<out T : Any>(
    override val active: T,
    override val backStack: Array<out T>
) : JsChildStack<T>

@JsExport
fun <C : Any, T : Any> Value<ChildStack<C, T>>.asJsStack(): JsValue<JsChildStack<T>> {
    val mappedValue: Value<JsChildStack<T>> = this.map { stack ->
        JsChildStackImpl(
            active = stack.active.instance,
            backStack = stack.backStack.map { it.instance }.toTypedArray()
        )
    }
    return JsValueImpl(mappedValue)
}


fun <T : Any> StateFlow<T>.asJsValue(
    initialValue: T,
    scope: CoroutineScope
): JsValue<T> {
    val valueRelay = MutableValue(initialValue)

    scope.launch {
        collect { newValue ->
            valueRelay.value = newValue
        }
    }

    return JsValueImpl(valueRelay)
}
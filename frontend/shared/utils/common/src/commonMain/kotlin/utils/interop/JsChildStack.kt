package utils.interop

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import kotlin.js.JsExport

@JsExport
interface JsChildStack<out T : Any> {
    val active: T
    val backStack: List<T>
}

class JsChildStackImpl<out T : Any>(
    override val active: T,
    override val backStack: List<T>
) : JsChildStack<T>

fun <C : Any, T : Any> Value<ChildStack<C, T>>.asJsStack(): JsValue<JsChildStack<T>> {
    val mappedValue: Value<JsChildStack<T>> = this.map { stack ->
        JsChildStackImpl(
            active = stack.active.instance,
            backStack = stack.backStack.map { it.instance }
        )
    }
    return JsValueImpl(mappedValue)
}

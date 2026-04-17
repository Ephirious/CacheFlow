package utils.interop

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import kotlin.js.JsExport

@JsExport
data class JsChildSlot<out T : Any>(
    val instance: T? = null,
)

fun <C : Any, T : Any> Value<ChildSlot<C, T>>.asJsSlot(): JsValue<JsChildSlot<T>> {
    val mappedValue: Value<JsChildSlot<T>> = this.map { slot ->
        JsChildSlot(instance = slot.child?.instance)
    }
    return JsValueImpl(mappedValue)
}
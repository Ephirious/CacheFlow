package utils.interop

import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import kotlin.js.JsExport

@JsExport
interface JsChildPages<out T : Any> {
    val active: T

    val items: List<T?>
    val selectedIndex: Int
}

class JsChildPagesImpl<out T : Any>(
    override val items: List<T?>,
    override val selectedIndex: Int
) : JsChildPages<T> {
    override val active: T
        get() = items[selectedIndex] ?: error("Active page instance is null")
}

fun <C : Any, T : Any> Value<ChildPages<C, T>>.asJsPages(): JsValue<JsChildPages<T>> {
    val mappedValue: Value<JsChildPages<T>> = this.map { pages ->
        JsChildPagesImpl(
            items = pages.items.map { it.instance },
            selectedIndex = pages.selectedIndex
        )
    }
    return JsValueImpl(mappedValue)
}
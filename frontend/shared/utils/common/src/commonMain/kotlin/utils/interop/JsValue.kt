package utils.interop

import com.arkivanov.decompose.value.Value

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
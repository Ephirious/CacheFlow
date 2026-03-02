package utils.presentation


@JsExport
interface JsChildStack<T : Any> {
    val active: T
}

@JsExport
interface JsValue<T : Any> {
    fun subscribe(observer: (T) -> Unit): JsDisposable
}

@JsExport
interface JsDisposable {
    fun dispose()
}

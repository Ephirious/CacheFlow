package utils

@JsModule("graphemer")
@JsNonModule
@JsName("default")
external class Graphemer {
    fun splitGraphemes(text: String): Array<String>
}

actual fun String.visualLength(): Int {
    val splitter = Graphemer()
    return splitter.splitGraphemes(this).size
}

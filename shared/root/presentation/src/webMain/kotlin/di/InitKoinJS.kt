package di

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

@JsName("initKoinJS")
@JsExport
fun initKoinJS(): Promise<Unit> = MainScope().promise {
    initKoin()
}
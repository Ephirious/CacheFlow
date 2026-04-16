package utils.presentation

import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import utils.interop.JsChildPages
import utils.interop.JsValue
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface DefaultPages<Config : Any, Child : Any> : BackHandlerOwner {
    @JsExport.Ignore
    val nav: PagesNavigation<Config>

    @JsExport.Ignore
    val pages: Value<ChildPages<Config, Child>>

    @JsName("childPages")
    val jsPages: JsValue<JsChildPages<Child>>

    @Suppress("unused")
    fun selectPage(index: Int) {
        nav.select(index)
    }
}
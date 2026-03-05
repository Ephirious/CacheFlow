package utils.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import utils.interop.JsChildStack
import utils.interop.JsValue
import kotlin.reflect.KClass

@JsExport
interface DefaultStack<Config : Any, Child : Any> : BackHandlerOwner {
    @JsExport.Ignore
    val nav: StackNavigation<Config>

    @JsExport.Ignore
    val stack: Value<ChildStack<Config, Child>>

    @JsName("childStack")
    val jsStack: JsValue<JsChildStack<Child>>

    fun onBackClicked() {
        popOnce(stack.active.instance::class)
    }

    @JsExport.Ignore
    fun popOnce(
        child: KClass<out Child>
    ) {
        if (child.isInstance(stack.active.instance)) {
            nav.pop()
        }
    }
}
package utils.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlin.reflect.KClass

@JsExport
interface DefaultStack<Config : Any, Child : Any> : BackHandlerOwner {
    val nav: StackNavigation<Config>

    val stack: Value<ChildStack<Config, Child>>

    fun onBackClicked() {
        popOnce(stack.active.instance::class)
    }

    fun popOnce(
        child: KClass<out Child>
    ) {
        if (child.isInstance(stack.active.instance)) {
            nav.pop()
        }
    }

    val activeChild: Child get() = stack.value.active.instance

    fun observeJsStack(
        observer: (JsChildStack<Child>) -> Unit
    ): JsDisposable {
        val disposable = stack.subscribe { stack ->
            observer(object : JsChildStack<Child> {
                override val active = stack.active.instance
            })
        }
        return object : JsDisposable {
            override fun dispose() = disposable.cancel()
        }
    }
}
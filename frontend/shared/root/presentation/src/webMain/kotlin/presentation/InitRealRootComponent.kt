package presentation

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.webhistory.withWebHistory
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import root.RealRootComponent
import root.RootComponent
import utils.Url


@JsExport
fun initRealRootComponent(): RootComponent {
    val lifecycle = LifecycleRegistry()

    val root =
        withWebHistory { stateKeeper, deepLink ->
            RealRootComponent(
                componentContext = DefaultComponentContext(
                    lifecycle = lifecycle,
                    stateKeeper = stateKeeper
                ),
                deepLinkUrl = deepLink?.let(::Url),
            )
        }

    lifecycle.attachToDocument()

    return root
}

private fun LifecycleRegistry.attachToDocument() {
    fun onVisibilityChanged() {
        val isVisible = document.asDynamic().visibilityState == "visible"
        if (isVisible) {
            resume()
        } else {
            stop()
        }
    }

    onVisibilityChanged()


    document.addEventListener(type = "visibilitychange", callback = { onVisibilityChanged() })
}
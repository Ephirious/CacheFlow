package presentation

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import root.RealRootComponent


@JsExport
fun initRealRootComponent(): RealRootComponent {
    val lifecycle = LifecycleRegistry()

    val root =
//        withWebHistory { stateKeeper, deepLink ->
        RealRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle), //, stateKeeper = stateKeeper
//                featureInstaller = DefaultFeatureInstaller,
//                deepLinkUrl = deepLink?.let(::Url),
        )
//        }

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
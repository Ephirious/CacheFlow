package startup

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.webhistory.withWebHistory
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import root.RealRootComponent
import root.RootComponent
import utils.Logg
import utils.Url


@OptIn(ExperimentalDecomposeApi::class)
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


    Logg.debug { "RealRootComponent initialized" }

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
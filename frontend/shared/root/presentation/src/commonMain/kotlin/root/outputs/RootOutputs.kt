package root.outputs

import com.arkivanov.decompose.router.stack.pushToFront
import root.RootComponent
import root.RootConfig
import root.RootOutput

fun RootComponent.onRootOutput(
    output: RootOutput,
) {
    when (output) {
        RootOutput.NavigateToMain -> nav.pushToFront(RootConfig.Main)
        RootOutput.NavigateToSettings -> nav.pushToFront(RootConfig.Settings(null))
        RootOutput.NavigateToStats -> nav.pushToFront(RootConfig.Stats)
    }
}
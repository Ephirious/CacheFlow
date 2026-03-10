package root.outputs

import com.arkivanov.decompose.router.stack.pushToFront
import root.RootComponent
import root.RootConfig
import root.RootOutput

fun RootComponent.onRootOutput(
    output: RootOutput,
) {
    when (output) {
        RootOutput.NavigateToInteropTest -> nav.pushToFront(RootConfig.InteropTest)
        RootOutput.NavigateToMain -> nav.pushToFront(RootConfig.Main)
        RootOutput.NavigateToSettings -> nav.pushToFront(RootConfig.Settings)
        RootOutput.NavigateToStats -> nav.pushToFront(RootConfig.Stats)
    }
}
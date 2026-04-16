package root.outputs

import com.arkivanov.decompose.router.pages.select
import root.RootComponent
import root.RootConfig
import root.RootOutput

fun RootComponent.onRootOutput(
    output: RootOutput,
) {
    when (output) {
        RootOutput.NavigateToMain -> nav.select(RootConfig.Main.INDEX)
        RootOutput.NavigateToSettings -> nav.select(RootConfig.Settings.INDEX)
        RootOutput.NavigateToStats -> nav.select(RootConfig.Stats.INDEX)
    }
}
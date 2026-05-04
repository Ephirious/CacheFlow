package root.outputs

import com.arkivanov.decompose.router.pages.select
import root.RootComponent
import root.RootConfig
import root.RootOutput

fun RootComponent.onRootOutput(
    output: RootOutput,
) {
    when (output) {
        RootOutput.NavigateToMain -> nav.select(RootConfig.Main.index)
        RootOutput.NavigateToSettings -> nav.select(RootConfig.Settings(null).index)
        RootOutput.NavigateToStats -> nav.select(RootConfig.Stats.index)
    }
}
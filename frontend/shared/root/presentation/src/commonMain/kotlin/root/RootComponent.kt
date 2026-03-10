package root


import interopSampleFlow.InteropSampleFlowComponent
import main.MainComponent
import settings.SettingsComponent
import stats.StatsComponent
import utils.presentation.DefaultStack
import kotlinx.serialization.Serializable as Serializable


@JsExport
interface RootComponent : DefaultStack<RootConfig, RootChild> {
    fun onOutput(output: RootOutput)
}

@Serializable
sealed interface RootConfig {
    @Serializable
    data object InteropTest : RootConfig

    @Serializable
    data object Main : RootConfig

    @Serializable
    data object Stats : RootConfig

    @Serializable
    data object Settings : RootConfig
}

@JsExport
sealed class RootOutput {
    data object NavigateToMain : RootOutput()
    data object NavigateToStats : RootOutput()
    data object NavigateToSettings : RootOutput()
    data object NavigateToInteropTest : RootOutput()
}

@JsExport
sealed class RootChild {
    @Suppress("unused")
    class InteropSampleFlowChild(val component: InteropSampleFlowComponent) : RootChild()

    @Suppress("unused")
    class MainChild(val component: MainComponent) : RootChild()

    @Suppress("unused")
    class StatsChild(val component: StatsComponent) : RootChild()

    @Suppress("unused")
    class SettingsChild(val component: SettingsComponent) : RootChild()
}
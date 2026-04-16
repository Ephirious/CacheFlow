package root


import kotlinx.serialization.Serializable
import main.MainComponent
import settings.SettingsComponent
import stats.StatsComponent
import utils.presentation.DefaultPages
import kotlin.js.JsExport


@JsExport
interface RootComponent : DefaultPages<RootConfig, RootChild> {
    fun onOutput(output: RootOutput)
}

@Serializable
sealed class RootConfig(val index: Int) {

    companion object {
        val list: List<RootConfig> = listOf(Main, Stats, Settings).sortedBy { it.index }
    }

    @Serializable
    data object Main : RootConfig(0)

    @Serializable
    data object Stats : RootConfig(1)

    @Serializable
    data object Settings : RootConfig(2)
}

@JsExport
sealed class RootOutput {
    data object NavigateToMain : RootOutput()
    data object NavigateToStats : RootOutput()
    data object NavigateToSettings : RootOutput()
}

@JsExport
sealed class RootChild {

    @Suppress("unused")
    class MainChild(val component: MainComponent) : RootChild()

    @Suppress("unused")
    class StatsChild(val component: StatsComponent) : RootChild()

    @Suppress("unused")
    class SettingsChild(val component: SettingsComponent) : RootChild()
}
package root


import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import kotlinx.serialization.Serializable
import main.MainComponent
import settings.SettingsComponent
import stats.StatsComponent
import utils.Url
import utils.presentation.DefaultStack
import kotlin.js.JsExport


@JsExport
interface RootComponent : DefaultStack<RootConfig, RootChild>, WebNavigationOwner {
    fun onOutput(output: RootOutput)
}

@Serializable
sealed class RootConfig(val index: Int) {

    companion object {
        val list: (Settings) -> List<RootConfig> = { settings ->
            listOf(Main, Stats, settings).sortedBy { it.index }
        }
    }

    @Serializable
    data object Main : RootConfig(0)

    @Serializable
    data object Stats : RootConfig(1)

    @Serializable
    data class Settings(val deepLinkUrl: Url?) : RootConfig(2)
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
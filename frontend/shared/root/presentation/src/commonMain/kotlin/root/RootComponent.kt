package root


import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import main.MainComponent
import settings.SettingsComponent
import stats.StatsComponent
import utils.presentation.DefaultPages
import kotlin.js.JsExport
import kotlinx.serialization.Serializable as Serializable


@JsExport
interface RootComponent : DefaultPages<RootConfig, RootChild>, WebNavigationOwner {
    fun onOutput(output: RootOutput)
}

@Serializable
sealed interface RootConfig {

    @Serializable
    data object Main : RootConfig {
        const val INDEX = 0
    }

    @Serializable
    data object Stats : RootConfig {
        const val INDEX = 1
    }

    @Serializable
    data object Settings : RootConfig {
        const val INDEX = 2
    }
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
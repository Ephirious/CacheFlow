package interopSampleFlow

import interopSample.models.Weather
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

@JsExport
data class InteropSampleFlowState(
    val weatherState: WeatherState,
    val sampleText: String,
) : MVIState {
    sealed class WeatherState : MVIState {
        data object Loading : WeatherState()
        data class OK(
            val weather: Weather
        ) : WeatherState()

        data class Error(val error: String) : WeatherState()
    }
}


@JsExport
sealed class InteropSampleFlowIntent : MVIIntent {
    data object ClickedRefresh : InteropSampleFlowIntent()
    data class ChangedSampleText(val text: String) : InteropSampleFlowIntent()
}
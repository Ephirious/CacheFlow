package interopSampleFlow

import interopSample.models.Weather
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

@JsExport
sealed class InteropSampleFlowState : MVIState {
    data object Loading : InteropSampleFlowState()
    data class OK(
        val weather: Weather
    ) : InteropSampleFlowState()

    data class Error(val error: String) : InteropSampleFlowState()
}

@JsExport
sealed class InteropSampleFlowIntent : MVIIntent {
    data object ClickedRefresh : InteropSampleFlowIntent()
}
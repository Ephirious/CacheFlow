package interopSampleFlow.interopSample

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

@JsExport
data class InteropSampleState(
    val text: String,
    val seconds: Int
) : MVIState

@JsExport
sealed class InteropSampleIntent : MVIIntent {
    data class ChangedText(val text: String) : InteropSampleIntent()
}
package interopSample.mvi

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import kotlin.js.JsExport

@JsExport
data class InteropSampleState(
    val text: String,
    val seconds: Int
) : MVIState

@JsExport
sealed class InteropSampleIntent : MVIIntent {
    data class ChangedText(val text: String) : InteropSampleIntent()
}
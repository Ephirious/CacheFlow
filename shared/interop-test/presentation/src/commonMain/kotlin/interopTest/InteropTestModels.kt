package interopTest

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

@JsExport
sealed class InteropTestState : MVIState {
    @Suppress("unused")
    data object Loading : InteropTestState()
    data class OK(
        val text: String
    ) : InteropTestState()

    data class Error(val error: String) : InteropTestState()
}

@JsExport
sealed class InteropTestIntent : MVIIntent {
    data class ChangedText(val text: String) : InteropTestIntent()
}
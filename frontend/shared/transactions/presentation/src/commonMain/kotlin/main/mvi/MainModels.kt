package main.mvi

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import kotlin.js.JsExport

@JsExport
sealed class MainState : MVIState {
    data class Error(val message: String) : MainState()
    data object OK : MainState()
}

@JsExport
sealed class MainIntent : MVIIntent {
    data class ThrowError(val message: String) : MainIntent()
}
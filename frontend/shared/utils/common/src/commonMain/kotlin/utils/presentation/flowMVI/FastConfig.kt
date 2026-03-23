package utils.presentation.flowMVI

import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.StoreBuilder
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.resetStateOnStop
import utils.AppConfig

@FlowMVIDSL
fun <S : MVIState, I : MVIIntent, A : MVIAction> StoreBuilder<S, I, A>.fastConfig(
    name: String,
    isDebuggable: Boolean = AppConfig.isDebuggable,
    resetOnStop: Boolean,
    doOnRecover: (S.(Exception) -> S)?
) {
    configure {
        this.name = name
        debuggable = isDebuggable
    }

    enableLogging()

    if (resetOnStop) {
        resetStateOnStop()
    }

    if (doOnRecover != null) {
        recover {
            updateState { doOnRecover(it) }
            null
        }
    }
}

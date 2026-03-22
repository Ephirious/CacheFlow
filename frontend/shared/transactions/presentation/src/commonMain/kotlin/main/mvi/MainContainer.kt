package main.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.reduce
import utils.AppConfig

class MainContainer(
) : Container<MainState, MainIntent, Nothing> {
    override val store: Store<MainState, MainIntent, Nothing> =
        store(
            initial = MainState.OK
        ) {
            configure {
                name = "Main"
                debuggable = AppConfig.isDebuggable
            }
            enableLogging()


            reduce { intent ->
                when (intent) {
                    is MainIntent.ThrowError -> {
                        updateState { MainState.Error(intent.message) }
                    }
                }
            }
        }
}
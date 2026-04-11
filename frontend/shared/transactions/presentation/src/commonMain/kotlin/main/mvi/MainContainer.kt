package main.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig

class MainContainer(
) : Container<MainState, MainIntent, Nothing> {
    override val store: Store<MainState, MainIntent, Nothing> =
        store(
            initial = MainState.OK
        ) {
            fastConfig(
                name = "Main",
                resetOnStop = false,
                doOnRecover = null
            )


            customReduce { intent ->
                when (intent) {
                    is MainIntent.ThrowError -> {
                        updateState { MainState.Error(intent.message) }
                    }
                }
            }
        }
}
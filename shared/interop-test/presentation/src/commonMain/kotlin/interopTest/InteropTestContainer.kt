package interopTest

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.resetStateOnStop

private typealias Ctx = PipelineContext<InteropTestState, InteropTestIntent, Nothing>


class InteropTestContainer : Container<InteropTestState, InteropTestIntent, Nothing> {
    override val store: Store<InteropTestState, InteropTestIntent, Nothing> =
        store(initial = InteropTestState.OK(text = "")) {
            configure {
                name = "InteropTest"
                debuggable = true
            }
            init {
                withState {
                    println("init: $this")
                }
            }
            enableLogging()

            resetStateOnStop()
            recover {
                updateState { InteropTestState.Error(it.message ?: "unknown error!") }
                null
            }
            reduce { intent ->
                when (intent) {
                    is InteropTestIntent.ChangedText -> updateStateImmediate<InteropTestState.OK, _> {
                        copy(text = intent.text)
                    }
                }
            }
        }
}
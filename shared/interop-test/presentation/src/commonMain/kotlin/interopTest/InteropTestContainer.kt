package interopTest

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce

private typealias Ctx = PipelineContext<InteropTestState, InteropTestIntent, Nothing>

@JsExport
class InteropTestContainer(

) : Container<InteropTestState, InteropTestIntent, Nothing> {
    @Suppress("NON_EXPORTABLE_TYPE")
    override val store: Store<InteropTestState, InteropTestIntent, Nothing> =
        store(initial = InteropTestState.OK(text = "")) {
            configure {
                name = "InteropTest"
                debuggable = true
            }
            enableLogging()
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
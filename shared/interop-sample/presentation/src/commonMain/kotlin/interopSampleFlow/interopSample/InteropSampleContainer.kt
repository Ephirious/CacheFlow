package interopSampleFlow.interopSample

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import utils.AppConfig

//private typealias Ctx = PipelineContext<InteropSampleState, InteropSampleIntent, Nothing>

class InteropSampleContainer : Container<InteropSampleState, InteropSampleIntent, Nothing> {
    override val store: Store<InteropSampleState, InteropSampleIntent, Nothing> =
        store(initial = InteropSampleState(text = "")) {
            configure {
                name = "InteropSample"
                debuggable = AppConfig.isDebuggable
            }
            enableLogging()

            recover {
                updateState { this.copy(text = "error!") }
                null
            }
            reduce { intent ->
                when (intent) {
                    is InteropSampleIntent.ChangedText -> updateStateImmediate {
                        this.copy(text = intent.text)
                    }
                }
            }
        }
}
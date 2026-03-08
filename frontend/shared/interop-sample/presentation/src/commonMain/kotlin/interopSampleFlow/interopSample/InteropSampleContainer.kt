package interopSampleFlow.interopSample

import kotlinx.coroutines.delay
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import utils.AppConfig
import kotlin.time.Duration.Companion.seconds

//private typealias Ctx = PipelineContext<InteropSampleState, InteropSampleIntent, Nothing>

class InteropSampleContainer : Container<InteropSampleState, InteropSampleIntent, Nothing> {
    override val store: Store<InteropSampleState, InteropSampleIntent, Nothing> =
        store(initial = InteropSampleState(text = "", seconds = 0)) {
            configure {
                name = "InteropSample"
                debuggable = AppConfig.isDebuggable
            }
//            enableLogging()

            recover {
                updateState { this.copy(text = "error!") }
                null
            }
            whileSubscribed(stopDelay = 0.seconds) {
                while (true) {
                    delay(1.seconds)
                    updateState {
                        this.copy(seconds = seconds + 1)
                    }
                }
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
package interopSample.mvi

import kotlinx.coroutines.delay
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.whileSubscribed
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import kotlin.time.Duration.Companion.seconds

//private typealias Ctx = PipelineContext<InteropSampleState, InteropSampleIntent, Nothing>

class InteropSampleContainer : Container<InteropSampleState, InteropSampleIntent, Nothing> {
    override val store: Store<InteropSampleState, InteropSampleIntent, Nothing> =
        store(initial = InteropSampleState(text = "", seconds = 0)) {

            fastConfig(
                name = "InteropSample", isDebuggable = false, resetOnStop = false,
                doOnRecover = { this.copy(text = it.message.orUnknown) }
            )

            whileSubscribed(stopDelay = 0.seconds) {
                while (true) {
                    delay(1.seconds)
                    updateState {
                        this.copy(seconds = seconds + 1)
                    }
                }
            }

            customReduce { intent ->
                when (intent) {
                    is InteropSampleIntent.ChangedText -> updateStateImmediate {
                        this.copy(text = intent.text)
                    }
                }
            }
        }
}
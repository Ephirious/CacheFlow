package interopSampleFlow

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import utils.AppConfig
import utils.presentation.AsyncDispatcher

private typealias Ctx = PipelineContext<InteropSampleFlowState, InteropSampleFlowIntent, Nothing>


class InteropSampleFlowContainer : Container<InteropSampleFlowState, InteropSampleFlowIntent, Nothing> {
    override val store: Store<InteropSampleFlowState, InteropSampleFlowIntent, Nothing> =
        store(initial = InteropSampleFlowState.Loading) {
            configure {
                name = "InteropSampleFlow"
                debuggable = AppConfig.isDebuggable
            }
            enableLogging()

            recover {
                updateState { InteropSampleFlowState.Error(it.message ?: "unknown error!") }
                null
            }
            init {
                loadWeather()
            }
            reduce { intent ->
                when (intent) {
                    InteropSampleFlowIntent.ClickedRefresh ->
                        loadWeather()
                }
            }
        }


    private var loadWeatherJob: Job? = null
    private fun Ctx.loadWeather() {
        if (loadWeatherJob?.isActive == true) return

        loadWeatherJob = launch(AsyncDispatcher) {
            updateState {
                InteropSampleFlowState.Loading
            }
            // todo load
        }
    }
}
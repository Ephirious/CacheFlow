package interopSampleFlow

import interopSample.usecases.GetWeatherUseCase
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


class InteropSampleFlowContainer(
    private val getWeatherUseCase: GetWeatherUseCase
) : Container<InteropSampleFlowState, InteropSampleFlowIntent, Nothing> {
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
                loadWeather(false)
            }
            reduce { intent ->
                when (intent) {
                    InteropSampleFlowIntent.ClickedRefresh ->
                        loadWeather(true)
                }
            }
        }


    private var loadWeatherJob: Job? = null
    private fun Ctx.loadWeather(fromNetwork: Boolean) {
        if (loadWeatherJob?.isActive == true) return

        loadWeatherJob = launch(AsyncDispatcher) {
            if (fromNetwork) {
                updateState {
                    InteropSampleFlowState.Loading
                }
            }

            updateState {
                val weather = getWeatherUseCase(fromNetwork)
                InteropSampleFlowState.OK(weather = weather)
            }
        }
    }
}
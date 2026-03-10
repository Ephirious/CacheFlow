package interopSampleFlow.mvi

import interopSample.usecases.GetWeatherFlowUseCase
import interopSample.usecases.ManageSampleTextUseCases
import interopSample.usecases.RefreshWeatherUseCase
import interopSampleFlow.mvi.InteropSampleFlowState.WeatherState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import utils.AppConfig
import utils.presentation.AsyncDispatcher

private typealias Ctx = PipelineContext<InteropSampleFlowState, InteropSampleFlowIntent, Nothing>


class InteropSampleFlowContainer(
    private val getWeatherFlowUseCase: GetWeatherFlowUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val manageSampleTextUseCases: ManageSampleTextUseCases,
) : Container<InteropSampleFlowState, InteropSampleFlowIntent, Nothing> {
    override val store: Store<InteropSampleFlowState, InteropSampleFlowIntent, Nothing> =
        store(
            initial = InteropSampleFlowState(
                weatherState = WeatherState.Loading,
                sampleText = manageSampleTextUseCases.getSampleText()
            )
        ) {
            configure {
                name = "InteropSampleFlow"
                debuggable = AppConfig.isDebuggable
            }
            enableLogging()

            recover {
                updateState { copy(weatherState = WeatherState.Error(it.message ?: "unknown error!")) }
                null
            }

            whileSubscribed {
                startWeatherSubscription()
            }

            reduce { intent ->
                when (intent) {
                    InteropSampleFlowIntent.ClickedRefresh ->
                        refreshWeather()

                    is InteropSampleFlowIntent.ChangedSampleText -> {
                        manageSampleTextUseCases.setSampleText(intent.text)
                        updateStateImmediate { copy(sampleText = intent.text) }
                    }
                }
            }
        }


    private var refreshWeatherJob: Job? = null
    private fun Ctx.refreshWeather() {
        if (refreshWeatherJob?.isActive == true) return

        refreshWeatherJob = launch(AsyncDispatcher) {
            updateState {
                copy(weatherState = WeatherState.Loading)
            }
            refreshWeatherUseCase()
            startWeatherSubscription()
        }
    }

    private var weatherSubscriptionJob: Job? = null


    private fun Ctx.startWeatherSubscription() {
        if (weatherSubscriptionJob?.isActive == true) return
        weatherSubscriptionJob = launch(AsyncDispatcher) {
            try {
                getWeatherFlowUseCase().collect { weather ->
                    updateState { copy(weatherState = WeatherState.OK(weather)) }
                }
            } catch (_: NullPointerException) {
                throw NullPointerException("Нет оффлайн данных (ошибка!!)")
            }
        }
    }
}
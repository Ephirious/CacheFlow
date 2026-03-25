package interopSampleFlow.mvi

import interopSample.usecases.GetWeatherFlowUseCase
import interopSample.usecases.ManageSampleTextUseCases
import interopSample.usecases.RefreshWeatherUseCase
import interopSampleFlow.mvi.InteropSampleFlowState.WeatherState
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import utils.orUnknown
import utils.presentation.AsyncDispatcher
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.registerOrIgnore

private typealias Ctx = PipelineContext<InteropSampleFlowState, InteropSampleFlowIntent, Nothing>


private enum class Jobs {
    ObserveWeather, RefreshWeather
}

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
            fastConfig(
                name = "InteropSampleFlow",
                resetOnStop = false,
                doOnRecover = { copy(weatherState = WeatherState.Error(it.message.orUnknown)) }
            )

            val jobs = JobManager<Jobs>()

            whileSubscribed {
                startWeatherSubscription(jobs)
            }

            reduce { intent ->
                when (intent) {
                    InteropSampleFlowIntent.ClickedRefresh ->
                        refreshWeather(jobs)

                    is InteropSampleFlowIntent.ChangedSampleText -> {
                        manageSampleTextUseCases.setSampleText(intent.text)
                        updateStateImmediate { copy(sampleText = intent.text) }
                    }
                }
            }
        }


    private fun Ctx.refreshWeather(jobs: JobManager<Jobs>) {
        launch(AsyncDispatcher) {
            updateState {
                copy(weatherState = WeatherState.Loading)
            }
            refreshWeatherUseCase()
            startWeatherSubscription(jobs)
        }.registerOrIgnore(jobs, Jobs.RefreshWeather)
    }

    private fun Ctx.startWeatherSubscription(jobs: JobManager<Jobs>) {
        launch(AsyncDispatcher) {
            try {
                getWeatherFlowUseCase().collect { weather ->
                    updateState { copy(weatherState = WeatherState.OK(weather)) }
                }
            } catch (_: NullPointerException) {
                throw NullPointerException("Нет оффлайн данных (ошибка!!)")
            }
        }.registerOrIgnore(jobs, Jobs.ObserveWeather)
    }

}
package interopSampleFlow.mvi

import interopSample.usecases.GetWeatherFlowUseCase
import interopSample.usecases.ManageSampleTextUseCases
import interopSample.usecases.RefreshWeatherUseCase
import interopSampleFlow.mvi.InteropSampleFlowState.WeatherState
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.whileSubscribed
import utils.orUnknown
import utils.presentation.AsyncDispatcher
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.launchOrIgnore
import utils.presentation.flowMVI.observe

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

            customReduce { intent ->
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
        launchOrIgnore(
            AsyncDispatcher,
            jobs, Jobs.RefreshWeather
        ) {
            updateState {
                copy(weatherState = WeatherState.Loading)
            }
            refreshWeatherUseCase()
            startWeatherSubscription(jobs)
        }
    }

    private fun Ctx.startWeatherSubscription(jobs: JobManager<Jobs>) {
        observe(
            flow = getWeatherFlowUseCase(),
            jobs = jobs,
            key = Jobs.ObserveWeather,
            onError = { error ->
                throw if (error is NullPointerException) NullPointerException("Нет оффлайн данных (ошибка!!)")
                else error
            }
        ) { weather ->
            updateState { copy(weatherState = WeatherState.OK(weather)) }
        }
    }

}
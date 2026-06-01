package settings.sync.mvi

import auth.TokenStorage
import auth.usecases.GetProfileUseCase
import auth.usecases.LogoutUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.withState
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.whileSubscribed
import sync.repositories.SyncManager
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.observe
import utils.presentation.flowMVI.registerOrIgnore
import kotlin.time.Duration.Companion.seconds

private typealias Ctx = PipelineContext<SyncOverviewState, SyncOverviewIntent, Nothing>


private enum class Jobs {
    UpdateAuthStatus, ObserveSyncStatus, CooldownTimer
}

class SyncOverviewContainer(
    private val logoutUseCase: LogoutUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val syncManager: SyncManager,
    private val tokenStorage: TokenStorage,
) : Container<SyncOverviewState, SyncOverviewIntent, Nothing> {

    private val defaultSyncCooldownDuration = 5

    @OptIn(DelicateStoreApi::class)
    override val store: Store<SyncOverviewState, SyncOverviewIntent, Nothing> =
        store(
            initial = SyncOverviewState.Loading
        ) {
            fastConfig(
                name = "SyncOverview", resetOnStop = false,
                doOnRecover = { _, _ ->
                    SyncOverviewState.NotAuthenticated
                }
            )

            val jobs = JobManager<Jobs>()

            init {
                updateAuthStatus(jobs)
            }

            whileSubscribed {
                observeSyncStatus(jobs)
            }

            customReduce { intent ->
                when (intent) {
                    SyncOverviewIntent.ExportCSV -> TODO()
                    SyncOverviewIntent.ForceSync -> {
                        withState<SyncOverviewState.Authenticated, _> {
                            if (forceSyncCooldownSeconds == 0) {
                                syncManager.forceSync(retry = false)
                                startCooldownTimer(jobs)
                            }
                        }
                    }

                    SyncOverviewIntent.Logout -> {
                        logoutUseCase()
                        jobs.cancel(Jobs.UpdateAuthStatus)
                        jobs.cancel(Jobs.CooldownTimer)
                        updateAuthStatus(jobs)
                    }

                    SyncOverviewIntent.UpdateAuthStatus -> updateAuthStatus(jobs)
                }
            }
        }

    private fun Ctx.startCooldownTimer(jobs: JobManager<Jobs>) {
        launch {
            for (seconds in defaultSyncCooldownDuration downTo 0) {
                updateState<SyncOverviewState.Authenticated, _> {
                    copy(forceSyncCooldownSeconds = seconds)
                }
                if (seconds > 0) {
                    delay(1.seconds)
                }
            }
        }.registerOrIgnore(manager = jobs, key = Jobs.CooldownTimer)
    }

    private fun Ctx.observeSyncStatus(jobs: JobManager<Jobs>) {
        observe(
            flow = syncManager.status, key = Jobs.ObserveSyncStatus, jobs = jobs
        ) { newSyncStatus ->
            updateState<SyncOverviewState.Authenticated, _> {
                copy(syncStatus = newSyncStatus)
            }
        }
    }


    private fun Ctx.updateAuthStatus(jobs: JobManager<Jobs>) {
        launch {
            if (tokenStorage.isTokensEmpty()) {
                jobs.cancel(Jobs.CooldownTimer)
                updateState { SyncOverviewState.NotAuthenticated }
                return@launch
            }
            withState {
                if (this !is SyncOverviewState.Authenticated) {
                    val profile = getProfileUseCase()

                    updateState {
                        SyncOverviewState.Authenticated(
                            name = profile.name,
                            email = profile.email,
                            id = profile.id,
                            syncStatus = syncManager.status.value
                        )
                    }
                }
            }
        }.registerOrIgnore(manager = jobs, key = Jobs.UpdateAuthStatus)
    }
}
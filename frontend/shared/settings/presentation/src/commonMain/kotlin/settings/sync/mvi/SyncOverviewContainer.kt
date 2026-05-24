package settings.sync.mvi

import auth.TokenStorage
import auth.usecases.GetProfileUseCase
import auth.usecases.LogoutUseCase
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.whileSubscribed
import sync.repositories.SyncManager
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.observe
import utils.presentation.flowMVI.registerOrIgnore

private typealias Ctx = PipelineContext<SyncOverviewState, SyncOverviewIntent, Nothing>


private enum class Jobs {
    UpdateAuthStatus, ObserveSyncStatus
}

class SyncOverviewContainer(
    private val logoutUseCase: LogoutUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val syncManager: SyncManager,
    private val tokenStorage: TokenStorage,
) : Container<SyncOverviewState, SyncOverviewIntent, Nothing> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<SyncOverviewState, SyncOverviewIntent, Nothing> =
        store(
            initial = SyncOverviewState.Loading
        ) {
            fastConfig(
                name = "SyncOverview", resetOnStop = true,
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
                        syncManager.forceSync()
                    }

                    SyncOverviewIntent.Logout -> {
                        logoutUseCase()
                        jobs.cancel(Jobs.UpdateAuthStatus)
                        updateAuthStatus(jobs)
                    }

                    SyncOverviewIntent.UpdateAuthStatus -> updateAuthStatus(jobs)
                }
            }
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
                updateState { SyncOverviewState.NotAuthenticated }
                return@launch
            }

            val profile = getProfileUseCase()

            updateState {
                SyncOverviewState.Authenticated(
                    name = profile.name,
                    email = profile.email,
                    id = profile.id,
                    syncStatus = syncManager.status.value
                )
            }
        }.registerOrIgnore(manager = jobs, key = Jobs.UpdateAuthStatus)
    }
}
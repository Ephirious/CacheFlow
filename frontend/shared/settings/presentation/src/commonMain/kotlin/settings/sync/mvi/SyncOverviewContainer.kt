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
import pro.respawn.flowmvi.plugins.init
import sync.repositories.SyncManager
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig

private typealias Ctx = PipelineContext<SyncOverviewState, SyncOverviewIntent, Nothing>

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

            init {
                updateAuthStatus()

                launch {
                    syncManager.status.collect { newSyncStatus ->
                        updateState<SyncOverviewState.Authenticated, _> {
                            copy(syncStatus = newSyncStatus)
                        }
                    }
                }
            }

            customReduce { intent ->
                when (intent) {
                    SyncOverviewIntent.ExportCSV -> TODO()
                    SyncOverviewIntent.ForceSync -> {
                        syncManager.forceSync()
                    }

                    SyncOverviewIntent.Logout -> {
                        logoutUseCase()
                        updateAuthStatus()
                    }

                    SyncOverviewIntent.UpdateAuthStatus -> updateAuthStatus()
                }
            }
        }


    private suspend fun Ctx.updateAuthStatus() {
        withState {

            if (tokenStorage.isTokensEmpty()) {
                updateState { SyncOverviewState.NotAuthenticated }
                return@withState
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
        }
    }
}
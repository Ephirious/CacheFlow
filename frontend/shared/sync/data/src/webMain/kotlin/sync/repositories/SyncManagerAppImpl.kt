package sync.repositories

import core.sqldelight.CustomSqlDriver
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.koin.core.component.inject
import sync.cloud.SyncRemoteDataSource
import sync.registerBackgroundSync
import utils.Logg
import utils.data.withWebLock
import utils.getServiceContainer

class SyncManagerAppImpl(
    remoteDataSource: SyncRemoteDataSource,
) : SyncManagerImplABC(
    remoteDataSource = remoteDataSource
) {
    private val sqlDriver: CustomSqlDriver by inject()


    override suspend fun requestSync() {
        scheduler.schedule()
    }

    override suspend fun forceSync() = trySync()

    private val scheduler =
        SyncScheduler(scope, listOf(/*todo*/))


    private val mutex = Mutex()

    init {
        scope.launch {
            scheduler.debouncedSyncEvents.collect {
                trySync()
            }
        }
        val serviceContainer = getServiceContainer()
        serviceContainer?.onmessage = { message ->
            (message.data as? String)?.let { data ->
                scope.launch {
                    Logg.debug { "Catch from service $data" }
                    when (val msg = decodeFromString<AppServiceMessage>(data)) {
                        AppServiceMessage.DBUpdated -> {
                            sqlDriver.reloadDb()
                        }

                        is AppServiceMessage.StatusChanged -> {
                            status.value = msg.status
                        }
                    }
                }
            }


        }
    }

    private suspend fun trySync() {
        mutex.withLock {
            withWebLock(scope) {
                syncWithStatusCallback { newStatus ->
                    status.value = newStatus
                    if (newStatus == SyncStatus.Failed) {
                        registerBackgroundSync()
                    }
                }
            }
        }
    }
}
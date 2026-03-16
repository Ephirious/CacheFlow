import org.koin.core.module.Module
import org.koin.dsl.module
import sync.cloud.SyncRemoteDataSource
import sync.repositories.SyncManager

val syncDataModule: ((SyncRemoteDataSource) -> SyncManager) -> Module = { getSyncManager ->
    module {
        single<SyncRemoteDataSource> { SyncRemoteDataSource(get()) }

        single<SyncManager> { getSyncManager(get()) }
    }
}
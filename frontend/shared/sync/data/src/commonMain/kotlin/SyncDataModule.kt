import org.koin.dsl.module
import sync.cloud.SyncRemoteDataSource
import sync.repositories.SyncManager
import sync.repositories.SyncManagerImpl

val syncDataModule = module {
    single<SyncRemoteDataSource> { SyncRemoteDataSource(get()) }

    single<SyncManager> { SyncManagerImpl(remoteDataSource = get()) }
}

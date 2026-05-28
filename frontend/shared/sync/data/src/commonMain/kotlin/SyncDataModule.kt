import org.koin.dsl.module
import sync.cloud.SyncRemoteDataSource
import sync.local.SyncLocalDataSource
import sync.repositories.SyncManager
import sync.repositories.SyncManagerImpl
import sync.repositories.SyncQueueRepository
import sync.repositories.SyncQueueRepositoryImpl
import sync.repositories.SyncRepository
import sync.repositories.SyncRepositoryImpl

val syncDataModule = module {
    single<SyncRemoteDataSource> { SyncRemoteDataSource(get()) }
    single<SyncQueueRepository> { SyncQueueRepositoryImpl(get(), get()) }


    single<SyncManager> {
        SyncManagerImpl(
            remoteDataSource = get(),
            queueRepo = get(),
            accountsRepo = get(),
            categoriesRepo = get(),
            localDataSource = get(),
            transactionsRepo = get(),
            tokenStorage = get(),
        )
    }

    single<SyncRepository> { SyncRepositoryImpl(get()) }

    single<SyncLocalDataSource> { SyncLocalDataSource(get()) }
}

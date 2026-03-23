package di

import sync.cloud.SyncRemoteDataSource
import sync.repositories.SyncManager
import sync.repositories.SyncManagerAppImpl

actual fun getSyncManagerApp(remoteDataSource: SyncRemoteDataSource): SyncManager =
    SyncManagerAppImpl(remoteDataSource)

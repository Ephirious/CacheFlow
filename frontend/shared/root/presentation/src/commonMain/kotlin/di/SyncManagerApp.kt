package di

import sync.cloud.SyncRemoteDataSource
import sync.repositories.SyncManager

expect fun getSyncManagerApp(remoteDataSource: SyncRemoteDataSource): SyncManager
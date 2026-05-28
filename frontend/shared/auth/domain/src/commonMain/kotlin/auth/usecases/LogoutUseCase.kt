package auth.usecases

import auth.AuthRepository
import sync.repositories.SyncRepository
import transactions.repositories.TransactionsRepository

class LogoutUseCase(
    private val repository: AuthRepository,
    private val transactionsRepository: TransactionsRepository,
    private val syncRepository: SyncRepository,
) {
    suspend operator fun invoke() {
        syncRepository.resetLastSyncDate()
        repository.logout()
        repository.clearAllTables()

        transactionsRepository.unsetFirstEntrance()
        transactionsRepository.setDbOnFirstEntranceAndTriggers(force = true)
    }
}
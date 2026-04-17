package editors.repositories

import editors.db.AccountsDatabaseDataSource
import editors.models.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import sync.repositories.SyncManager
import utils.presentation.AsyncDispatcher

class AccountsRepositoryImpl(
    private val dbDataSource: AccountsDatabaseDataSource,
    private val syncManager: SyncManager
) : AccountsRepository {
    override fun getAccountsFlow(): Flow<List<Account>> =
        dbDataSource.getAccountsFlow().flowOn(AsyncDispatcher)

    override suspend fun getAccountById(id: String): Account =
        dbDataSource.getAccountById(id)


}
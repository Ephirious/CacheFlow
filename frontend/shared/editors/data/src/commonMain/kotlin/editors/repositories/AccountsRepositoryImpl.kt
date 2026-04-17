package editors.repositories

import editors.db.AccountsDatabaseDataSource
import editors.models.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import sync.repositories.SyncManager
import utils.presentation.AsyncDispatcher
import utils.types.HexColor

class AccountsRepositoryImpl(
    private val dbDataSource: AccountsDatabaseDataSource,
    private val syncManager: SyncManager
) : AccountsRepository {
    override fun getAccountsFlow(): Flow<List<Account>> =
        dbDataSource.getAccountsFlow().flowOn(AsyncDispatcher)

    override suspend fun getAccountById(id: String): Account =
        dbDataSource.getAccountById(id)

    override suspend fun insertAccount(name: String, stringAmount: String, color: HexColor) =
        dbDataSource.insertAccount(name = name, stringAmount = stringAmount, color = color)

    override suspend fun updateAccount(id: String, name: String, color: HexColor) =
        dbDataSource.updateAccount(id = id, name = name, color = color)


}
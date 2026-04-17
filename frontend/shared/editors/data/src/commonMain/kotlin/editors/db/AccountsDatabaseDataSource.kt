package editors.db

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.AccountsQueries
import editors.mappers.listToDomain
import editors.mappers.toDomain
import editors.models.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import utils.presentation.AsyncDispatcher

class AccountsDatabaseDataSource(
    private val accountsQueries: AccountsQueries
) {
    fun getAccountsFlow(): Flow<List<Account>> {
        return accountsQueries.selectAll()
            .asFlow()
            .mapToList(AsyncDispatcher)
            .map { entity ->
                entity.listToDomain()
            }
    }

    suspend fun getAccountById(id: String): Account = accountsQueries.selectById(id).awaitAsOne().toDomain()
}
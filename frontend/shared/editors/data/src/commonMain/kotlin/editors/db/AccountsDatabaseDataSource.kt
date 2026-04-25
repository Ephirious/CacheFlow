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
import utils.types.BigDecimal
import utils.types.HexColor
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AccountsDatabaseDataSource(
    private val accountsQueries: AccountsQueries
) {
    fun getAccountsFlow(onlyActive: Boolean): Flow<List<Account>> {
        return (if (onlyActive) accountsQueries.selectActive() else accountsQueries.selectAll())
            .asFlow()
            .mapToList(AsyncDispatcher)
            .map { entity ->
                entity.listToDomain()
            }
    }

    suspend fun getAccountById(id: String): Account = accountsQueries.selectById(id).awaitAsOne().toDomain()


    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAccount(
        name: String,
        stringAmount: String,
        color: HexColor,
    ) {
        val funds = try {
            BigDecimal(stringAmount)
        } catch (_: Throwable) {
            BigDecimal.ZERO
        }

        val id = Uuid.generateV7().toString()
        accountsQueries.insert(id = id, name = name, funds = funds, color = color.normalizedHex)
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateAccount(
        id: String,
        name: String,
        color: HexColor,
    ) {
        accountsQueries.update(id = id, name = name, color = color.normalizedHex)
    }
}
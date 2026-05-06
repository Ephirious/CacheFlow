package editors.db

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.AccountsQueries
import data.OperationsQueries
import editors.mappers.listToDomain
import editors.mappers.toDomain
import editors.models.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import utils.presentation.AsyncDispatcher
import utils.toInstant
import utils.toLocalDate
import utils.types.BigDecimal
import utils.types.HexColor
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AccountsDatabaseDataSource(
    private val accountsQueries: AccountsQueries,
    private val transactionsQueries: OperationsQueries
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
        accountsQueries.transaction {
            val funds = try {
                BigDecimal(stringAmount)
            } catch (_: Throwable) {
                BigDecimal.ZERO
            }

            val accountId = Uuid.generateV7().toString()


            if (!funds.isZero) {
                transactionsQueries.upsert(
                    id = Uuid.generateV7().toString(),
                    account_uuid = accountId,
                    category_uuid = null,
                    transfer_id = null,
                    amount = funds,
                    date = Clock.System.now().toLocalDate().toInstant(),
                    notes = "Начальный баланс ($name)"
                )
            }

            accountsQueries.insert(id = accountId, name = name, funds = funds, color = color.normalizedHex)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateAccount(
        id: String,
        name: String,
        color: HexColor,
    ) {
        accountsQueries.update(id = id, name = name, color = color.normalizedHex)
    }

    suspend fun softDeleteAccount(
        id: String,
    ) {
        accountsQueries.softDelete(id)
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun upsertAccount(
        id: String,
        name: String,
        color: HexColor,
        stringAmount: String,
    ) {
        val funds = try {
            BigDecimal(stringAmount)
        } catch (e: Throwable) {
            BigDecimal.ZERO
        }

        accountsQueries.upsert(id = id, name = name, funds = funds, color = color.normalizedHex)
    }
}
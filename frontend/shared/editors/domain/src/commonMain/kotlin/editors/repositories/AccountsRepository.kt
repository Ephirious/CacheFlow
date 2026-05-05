package editors.repositories

import editors.models.Account
import kotlinx.coroutines.flow.Flow
import utils.types.HexColor

interface AccountsRepository {
    fun getAccountsFlow(): Flow<List<Account>>
    suspend fun getAccountById(id: String): Account

    suspend fun insertAccount(
        name: String,
        stringAmount: String,
        color: HexColor,
    )

    suspend fun updateAccount(
        id: String,
        name: String,
        color: HexColor,
    )

    suspend fun softDeleteAccount(id: String)

    suspend fun upsertAccount(id: String, name: String, color: HexColor, stringAmount: String)
}
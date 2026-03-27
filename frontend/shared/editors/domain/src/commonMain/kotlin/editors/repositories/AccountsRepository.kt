package editors.repositories

import editors.models.Account
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    fun getAccountsFlow(): Flow<List<Account>>
}
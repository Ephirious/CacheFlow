package auth.db

import data.CommonQueries

class AuthDatabaseDataSource(
    private val commonQueries: CommonQueries
) {
    suspend fun clearAllTables() {
        commonQueries.clearAllTables().await()
    }
}
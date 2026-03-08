package core.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

expect class SqlDriverFactory() {
    fun createDriver(
        schema: SqlSchema<QueryResult.AsyncValue<Unit>>, name: String
    ): SqlDriver
}
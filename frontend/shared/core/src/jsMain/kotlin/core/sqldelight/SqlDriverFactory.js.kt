package core.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema


actual class SqlDriverFactory actual constructor() {
    actual fun createDriver(
        schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
        name: String,
        isSW: Boolean
    ): SqlDriver =
        if (isSW)
            SqlJsServiceWorkerDriver(schema)
        else
            WebWorkerDriver()
}
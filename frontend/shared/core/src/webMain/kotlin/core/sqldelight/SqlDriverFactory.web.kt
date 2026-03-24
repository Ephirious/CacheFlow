package core.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import org.w3c.dom.MODULE
import org.w3c.dom.WorkerOptions
import org.w3c.dom.WorkerType


actual class SqlDriverFactory actual constructor() {
    actual fun createDriver(
        schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
        name: String,
        isSW: Boolean
    ): SqlDriver = CustomWebWorkerDriver(
        "/src/workers/sqljs.worker.js",
        options = WorkerOptions(type = WorkerType.MODULE)
    )
}
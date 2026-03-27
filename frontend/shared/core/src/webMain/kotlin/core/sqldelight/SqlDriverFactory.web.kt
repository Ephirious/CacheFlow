package core.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import core.sqldelight.fork.WebWorkerDriver
import org.w3c.dom.MODULE
import org.w3c.dom.Worker
import org.w3c.dom.WorkerOptions
import org.w3c.dom.WorkerType


actual class SqlDriverFactory actual constructor() {
    actual fun createDriver(
        schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
        name: String,
        isSW: Boolean
    ): SqlDriver = WebWorkerDriver(
        Worker(
            "/src/workers/sqljs.worker.js",
            options = WorkerOptions(type = WorkerType.MODULE)
        )
    )
}
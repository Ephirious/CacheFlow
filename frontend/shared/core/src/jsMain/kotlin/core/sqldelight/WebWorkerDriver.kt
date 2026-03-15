package core.sqldelight

import app.cash.sqldelight.driver.worker.WebWorkerDriver
import app.cash.sqldelight.driver.worker.expected.Worker

fun WebWorkerDriver() = WebWorkerDriver(
    Worker(
        "/db/sqljs.worker.js"
    )
)
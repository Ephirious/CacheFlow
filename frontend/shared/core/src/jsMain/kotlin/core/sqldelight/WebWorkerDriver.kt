package core.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker


class CustomWebWorkerDriver(
    private val worker: Worker
) : CustomSqlDriver, SqlDriver by WebWorkerDriver(worker = worker) {

    private val listeners = mutableMapOf<String, MutableSet<Query.Listener>>()

    override suspend fun reloadDb() {
        worker.postMessage(js("{ action: 'reload_db' }"))
        this.notifyListeners(*listeners.keys.toTypedArray())
    }

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) {
        queryKeys.forEach {
            listeners.getOrPut(it) { mutableSetOf() }.add(listener)
        }
    }

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) {
        queryKeys.forEach {
            listeners[it]?.remove(listener)
        }
    }

    override fun notifyListeners(vararg queryKeys: String) {
        queryKeys.flatMap { listeners[it].orEmpty() }
            .distinct()
            .forEach(Query.Listener::queryResultsChanged)
    }
}

fun getWebWorkerDriver() = CustomWebWorkerDriver(worker = Worker("/db/sqljs.worker.js"))

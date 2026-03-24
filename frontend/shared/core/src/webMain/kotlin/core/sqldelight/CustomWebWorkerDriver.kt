package core.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.BroadcastChannel
import org.w3c.dom.Worker
import org.w3c.dom.WorkerOptions
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class CustomWebWorkerDriver(
    private val worker: Worker
) : SqlDriver by WebWorkerDriver(worker = worker) {

    constructor(
        scriptURL: String,
        options: WorkerOptions
    ) : this(Worker(scriptURL = scriptURL, options = options))

    private val listeners = mutableMapOf<String, MutableSet<Query.Listener>>()


    init {

        val workerId = Uuid.generateV7().toString()

        worker.postMessage(js("{ action: 'set_worker_id', workerId: workerId }"))

        BroadcastChannel("sqlite_sync_channel").onmessage = { event ->
            val data = event.data.asDynamic()
            if (data.action == "db_updated" && data.senderId != workerId) {
                notifyListeners(*listeners.keys.toTypedArray())
            }
        }
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



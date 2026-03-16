package core.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.*
import core.sw.swSendMessagesToClients
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SqlJsServiceWorkerDriver(
    private val schema: SqlSchema<QueryResult.AsyncValue<Unit>>
) : CustomSqlDriver {

    private companion object {
        const val DB_NAME = "app_db"
        const val STORE_NAME = "table"

        const val STORAGE_NAME = "CacheFlowSqlStorage"
    }

    private var db: dynamic = null
    private val mutex = Mutex()

    private var transaction: Transaction? = null

    private var sql: dynamic = null


    private suspend fun ensureDb() {
        if (db != null) return

        mutex.withLock {
            if (db != null) return@withLock

            val initSqlJs = js("self.initSqlJs")
            if (sql == null) {
                sql = await(initSqlJs(js("{ locateFile: f => '/db/sql-wasm.wasm' }")))
            }
            val jsScopedSql = sql
            val savedData = getDbFromIndexedDB()

            db = if (savedData != null) {
                val uint8 = Uint8Array(savedData.toTypedArray())
                println("[INFO-ServiceWorker] Restoring DB from ${uint8.byteLength} bytes")
                js("new jsScopedSql.Database(uint8)")
            } else {
                println("[INFO-ServiceWorker] Creating fresh database: $jsScopedSql")
                js("new jsScopedSql.Database()")
            }


            if (savedData == null) {
                schema.create(this)
            }
        }
    }

    private suspend fun getDbFromIndexedDB(): ByteArray? = suspendCancellableCoroutine { cont ->
        val request = js("indexedDB").open(STORAGE_NAME, 1)

        request.onerror = { cont.resume(null) }
        request.onsuccess = { e: dynamic ->
            val dbWrapper = e.target.result

            run {
                if (!dbWrapper.objectStoreNames.contains(STORE_NAME)) {
                    cont.resume(null)
                    return@run
                }
            }

            val tx = dbWrapper.transaction(STORE_NAME, "readonly")
            val store = tx.objectStore(STORE_NAME)
            val req = store.get(DB_NAME)

            req.onsuccess = {
                val result = req.result
                if (result != null) {
                    cont.resume(result.unsafeCast<ByteArray>())
                } else {
                    println("[INFO-ServiceWorker] Key '$DB_NAME' not found in IndexedDB")
                    cont.resume(null)
                }
            }
            req.onerror = { cont.resume(null) }
        }
    }

    private fun saveDbToIndexedDB() {
        val data = db.export()
        val request = js("indexedDB").open(STORAGE_NAME, 1)
        request.onsuccess = { e: dynamic ->
            val tx = e.target.result.transaction(STORE_NAME, "readwrite")
            tx.objectStore(STORE_NAME).put(data, DB_NAME)
        }
        swSendMessagesToClients("{\"type\":\"db_updated\"}")
    }

    private suspend fun await(promise: dynamic): dynamic =
        suspendCancellableCoroutine { cont ->
            promise.then(
                { r: dynamic -> cont.resume(r) },
                { e: dynamic -> cont.resumeWithException(RuntimeException(e.toString())) }
            )
        }

    override fun <R> executeQuery(
        identifier: Int?,
        sql: String,
        mapper: (SqlCursor) -> QueryResult<R>,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?
    ): QueryResult<R> = QueryResult.AsyncValue {

        ensureDb()

        val ps = JsPrepared(parameters)
        binders?.invoke(ps)

        val results = db.exec(sql, ps.params())

        val cursor =
            if (results.length > 0)
                JsCursor(results[0])
            else
                JsCursor(null)

        mapper(cursor).await()
    }

    override fun execute(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?
    ): QueryResult<Long> = QueryResult.AsyncValue {

        ensureDb()

        val ps = JsPrepared(parameters)
        binders?.invoke(ps)

        val params = ps.params()
        println("[INFO-ServiceWorker] Executing '$sql' with params: ${JSON.stringify(params)}")

        if (parameters > 0)
            db.run(sql, params)
        else
            db.run(sql)

        0L
    }

    override fun newTransaction(): QueryResult<Transacter.Transaction> =
        QueryResult.AsyncValue {

            ensureDb()

            val tx = Transaction(transaction)
            transaction = tx

            if (tx.parent == null)
                db.run("BEGIN")

            tx
        }

    override fun currentTransaction(): Transacter.Transaction? = transaction

    private inner class Transaction(val parent: Transaction?) : Transacter.Transaction() {
        override val enclosingTransaction: Transacter.Transaction? = parent

        override fun endTransaction(successful: Boolean): QueryResult<Unit> = QueryResult.AsyncValue {
            if (parent == null) {
                try {
                    if (successful) db.run("COMMIT") else db.run("ROLLBACK")
                    saveDbToIndexedDB()
                } catch (_: dynamic) {

                }
            }
            transaction = parent
        }
    }


    override fun addListener(vararg queryKeys: String, listener: Query.Listener) {}

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) {}

    override fun notifyListeners(vararg queryKeys: String) {}

    override fun close() {
        db?.close()
        db = null
    }

    override suspend fun reloadDb() {
        if (db != null) {
            try {
                db.close()
            } catch (_: dynamic) {
            }
            db = null
        }

        ensureDb()
    }
}

private class JsPrepared(count: Int) : SqlPreparedStatement {

    private val params = arrayOfNulls<Any>(count)

    override fun bindLong(index: Int, long: Long?) {
        params[index] = long?.toDouble()
    }

    override fun bindString(index: Int, string: String?) {
        params[index] = string
    }

    override fun bindDouble(index: Int, double: Double?) {
        params[index] = double
    }

    override fun bindBoolean(index: Int, boolean: Boolean?) {
        params[index] = if (boolean == true) 1.0 else 0.0
    }

    override fun bindBytes(index: Int, bytes: ByteArray?) {
        params[index] = bytes
    }

    fun params(): dynamic {
        val jsArr = js("[]")
        for (p in params) {
            jsArr.push(p)
        }
        return jsArr
    }
}

private class JsCursor(
    result: dynamic
) : SqlCursor {

    private val rows =
        if (result != null && result.values != null)
            result.values
        else
            emptyArray<dynamic>()

    private var index = -1

    override fun next(): QueryResult<Boolean> {
        index++
        val size = rows.length.unsafeCast<Int>()
        return QueryResult.Value(index < size)
    }

    override fun getString(index: Int): String? =
        rows[this.index][index] as? String

    override fun getLong(index: Int): Long? =
        (rows[this.index][index] as? Double)?.toLong()

    override fun getDouble(index: Int): Double? =
        rows[this.index][index] as? Double

    override fun getBoolean(index: Int): Boolean =
        (rows[this.index][index] as? Double) == 1.0

    override fun getBytes(index: Int): ByteArray? {
        val v = rows[this.index][index] ?: return null
        val u = v.unsafeCast<Uint8Array>()
        return ByteArray(u.length) { i -> u[i] }
    }
}
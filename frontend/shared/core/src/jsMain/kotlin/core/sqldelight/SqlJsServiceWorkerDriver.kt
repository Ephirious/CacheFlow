package core.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlPreparedStatement
import app.cash.sqldelight.db.SqlSchema
import core.sw.swSendMessageToClients
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import utils.Logg
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

        val initSqlJs = js("self.initSqlJs")
        if (sql == null) {
            sql = await(initSqlJs(js($$"{locateFile: file => `${self.sqlWasmUrl}`}")))
        }
        val jsScopedSql = sql
        val savedData = getDbFromIndexedDB()

        db = if (savedData != null) {
            Logg.debug { "Restoring DB" }
            js("new jsScopedSql.Database(savedData)")
        } else {
            Logg.debug { "Creating fresh database: $jsScopedSql" }
            js("new jsScopedSql.Database()")
        }

        if (savedData == null) {
            schema.create(this)
        }

    }

    private suspend fun openIDB(): dynamic = suspendCancellableCoroutine { cont ->
        val request = js("indexedDB").open(STORAGE_NAME, 1)

        request.onupgradeneeded = { e: dynamic ->
            val idb = e.target.result
            if (!idb.objectStoreNames.contains(STORE_NAME)) {
                idb.createObjectStore(STORE_NAME)
            }
        }

        request.onsuccess = { e: dynamic -> cont.resume(e.target.result) }
        request.onerror = { _: dynamic -> cont.resumeWithException(RuntimeException("IDB Open Error")) }
    }

    private suspend fun getDbFromIndexedDB(): dynamic {
        val idb = openIDB()
        return suspendCancellableCoroutine { cont ->
            val tx = idb.transaction(STORE_NAME, "readonly")
            val store = tx.objectStore(STORE_NAME)
            val req = store.get(DB_NAME)

            req.onsuccess = {
                idb.close()
                cont.resume(req.result)
            }
            req.onerror = {
                idb.close()
                cont.resume(null)
            }
        }
    }

    private suspend fun saveDbToIndexedDB() {
        try {
            val data = db.export()
            val idb = openIDB()

            suspendCancellableCoroutine { cont ->
                val tx = idb.transaction(STORE_NAME, "readwrite")
                val store = tx.objectStore(STORE_NAME)

                tx.oncomplete = {
                    idb.close()
                    swSendMessageToClients("{\"type\":\"db_updated\"}")
                    cont.resume(Unit)
                }
                tx.onerror = {
                    idb.close()
                    cont.resume(Unit)
                }
                store.put(data, DB_NAME)
            }
        } catch (e: Exception) {
            Logg.error { "Failed to save DB: ${e.message}" }
        }
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
        mutex.withLock {
            ensureDb()
            val ps = JsPrepared(parameters)
            binders?.invoke(ps)
            val results = db.exec(sql, ps.params())
            val cursor = if (results.length > 0) JsCursor(results[0]) else JsCursor(null)
            mapper(cursor).await()
        }
    }

    override fun execute(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?
    ): QueryResult<Long> = QueryResult.AsyncValue {
        mutex.withLock {
            ensureDb()
            val ps = JsPrepared(parameters)
            binders?.invoke(ps)
            val params = ps.params()

            Logg.debug { "Executing '$sql' with params: ${JSON.stringify(params)}" }

            if (parameters > 0) db.run(sql, params) else db.run(sql)

            if (transaction == null) {
                saveDbToIndexedDB()
            }
            0L
        }
    }

    override fun newTransaction(): QueryResult<Transacter.Transaction> =
        QueryResult.AsyncValue {
            mutex.withLock {
                ensureDb()
                val tx = Transaction(transaction)
                transaction = tx
                if (tx.parent == null) db.run("BEGIN")
                tx
            }
        }

    override fun currentTransaction(): Transacter.Transaction? = transaction

    private inner class Transaction(val parent: Transaction?) : Transacter.Transaction() {
        override val enclosingTransaction: Transacter.Transaction? = parent
        override fun endTransaction(successful: Boolean): QueryResult<Unit> = QueryResult.AsyncValue {
            mutex.withLock {
                if (transaction != this@Transaction) return@AsyncValue

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
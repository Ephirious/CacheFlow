package core.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlPreparedStatement
import app.cash.sqldelight.db.SqlSchema
import core.sw.swSendMessageToClients
import kotlinx.coroutines.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import utils.Logg
import kotlin.js.Promise

class SqlJsServiceWorkerDriver(
    private val schema: SqlSchema<QueryResult.AsyncValue<Unit>>
) : CustomSqlDriver {

    private companion object {
        const val DB_KEY = "app_db"
        const val STORE_NAME = "table"
        const val IDB_NAME = "CacheFlowSqlStorage"
    }

    private var db: dynamic = null
    private var sqlite3: dynamic = null
    private var currentDbFileName: String? = null
    private val mutex = Mutex()
    private var transaction: Transaction? = null

    private suspend fun withIDB(block: suspend (dynamic) -> dynamic): dynamic {
        val openDB = js("self.openDB")
        val dbPromise: Promise<dynamic> = openDB(
            IDB_NAME, 1, js(
                """{
            upgrade(db) {
                if (!db.objectStoreNames.contains('$STORE_NAME')) {
                    db.createObjectStore('$STORE_NAME');
                }
            },
            blocking() { this.close(); }
        }"""
            )
        )

        val idb = dbPromise.await()
        return try {
            block(idb)
        } finally {
            idb.close()
        }
    }

    private suspend fun ensureDb() {
        if (db != null) return

        val s3 = if (sqlite3 == null) {
            val initFn = js("self.sqlite3InitModule")
            val result = (initFn() as Promise<dynamic>).await()
            sqlite3 = result
            result
        } else {
            sqlite3
        }

        val savedData = withIDB { idb ->
            idb.get(STORE_NAME, DB_KEY).unsafeCast<Promise<dynamic>>().await()
        }

        currentDbFileName?.let { oldFile ->
            try {
                s3.capi.sqlite3_js_posix_unlink(oldFile)
            } catch (_: dynamic) {
            }
        }

        db = if (savedData != null && savedData.byteLength.unsafeCast<Int>() > 100) {
            Logg.debug { "SQL:  IDB mode" }
            val tempName = "sw_live_${js("Date.now()")}.db"
            currentDbFileName = tempName
            s3.capi.sqlite3_js_posix_create_file(tempName, savedData)
            js("new s3.oo1.DB(tempName, 'c')")
        } else {
            Logg.debug { "SQL: IDB mode (fresh)" }
            currentDbFileName = null
            js("new s3.oo1.DB(':memory:', 'c')")
        }

        db.exec("PRAGMA journal_mode=DELETE; PRAGMA synchronous=OFF; PRAGMA auto_vacuum=INCREMENTAL;")

        if (savedData == null) {
            schema.create(this).await()
        }
    }

    private suspend fun saveDbToIndexedDB() {
        val currentDb = db ?: return
        val s3 = sqlite3 ?: return
        try {
            currentDb.exec("PRAGMA incremental_vacuum(0); PRAGMA shrink_memory;")
            val bytes = s3.capi.sqlite3_js_db_export(currentDb.pointer)

            if (bytes != null) {
                withIDB { idb ->
                    idb.put(STORE_NAME, bytes, DB_KEY).unsafeCast<Promise<Unit>>().await()
                }
                swSendMessageToClients("{\"type\":\"db_updated\"}")
            }
        } catch (e: Exception) {
            Logg.error { "SQL: Save failed: ${e.message}" }
        }
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
            val prepared = JsPrepared(parameters)
            binders?.invoke(prepared)
            val actualParams = prepared.params()
            val resultRows = db.exec(
                js(
                    """{
                sql: sql,
                bind: actualParams,
                rowMode: 'array',
                returnValue: 'resultRows'
            }"""
                )
            )

            mapper(JsCursor(resultRows)).await()
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
            val prepared = JsPrepared(parameters)
            binders?.invoke(prepared)
            val actualParams = prepared.params()

            db.exec(
                js(
                    """{
                sql: sql,
                bind: actualParams
            }"""
                )
            )

            if (transaction == null) {
                val isWrite =
                    js("/^\\s*(INSERT|UPDATE|DELETE|CREATE|DROP|ALTER|REPLACE)/i.test(sql)").unsafeCast<Boolean>()
                if (isWrite) saveDbToIndexedDB()
            }
            0L
        }
    }

    override fun newTransaction(): QueryResult<Transacter.Transaction> = QueryResult.AsyncValue {
        mutex.withLock {
            ensureDb()
            val tx = Transaction(transaction)
            transaction = tx
            if (tx.parent == null) db.exec("BEGIN TRANSACTION;")
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
                    if (successful) db.exec("COMMIT;") else db.exec("ROLLBACK;")
                    saveDbToIndexedDB()
                }
                transaction = parent
            }
        }
    }

    override suspend fun reloadDb() {
        mutex.withLock {
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

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) {}
    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) {}
    override fun notifyListeners(vararg queryKeys: String) {}
    override fun close() {
        db?.close()
        db = null
        currentDbFileName?.let { sqlite3?.capi?.sqlite3_js_posix_unlink(it) }
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
        if (bytes == null) {
            params[index] = null
            return
        }
        val u8 = Uint8Array(bytes.toTypedArray())
        params[index] = u8
    }

    fun params(): dynamic {
        val jsArr = js("[]")
        for (p in params) {
            jsArr.push(p)
        }
        return jsArr
    }
}

private class JsCursor(private val rows: dynamic) : SqlCursor {
    private var index = -1
    private val size = if (rows != null) rows.length.unsafeCast<Int>() else 0

    override fun next(): QueryResult<Boolean> = QueryResult.Value(++index < size)

    override fun getString(index: Int): String? = rows[this.index][index] as? String
    override fun getLong(index: Int): Long? = (rows[this.index][index] as? Double)?.toLong()
    override fun getDouble(index: Int): Double? = rows[this.index][index] as? Double
    override fun getBoolean(index: Int): Boolean = (rows[this.index][index] as? Double) == 1.0
    override fun getBytes(index: Int): ByteArray? {
        val u = rows[this.index][index]?.unsafeCast<Uint8Array>() ?: return null
        return ByteArray(u.length) { i -> u[i] }
    }
}
package core.sqldelight.fork

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array

internal class WorkerSqlCursor(result: WorkerResult) : SqlCursor {
    private val values: Array<Array<dynamic>> = result.values
    private var currentRow = -1

    override fun next(): QueryResult<Boolean> = QueryResult.Value(++currentRow < values.size)

    override fun getString(index: Int): String? = values[currentRow][index].unsafeCast<String?>()

    override fun getLong(index: Int): Long? = (values[currentRow][index] as? Double)?.toLong()

    override fun getBytes(index: Int): ByteArray? =
        (values[currentRow][index] as? Uint8Array)?.let { Int8Array(it.buffer).unsafeCast<ByteArray>() }

    override fun getDouble(index: Int): Double? = values[currentRow][index].unsafeCast<Double?>()

    override fun getBoolean(index: Int): Boolean? = values[currentRow][index].unsafeCast<Boolean?>()
}
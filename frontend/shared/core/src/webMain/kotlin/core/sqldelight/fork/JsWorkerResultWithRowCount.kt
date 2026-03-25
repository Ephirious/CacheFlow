package core.sqldelight.fork

internal class JsWorkerResultWithRowCount(
    private val data: JsWorkerResponse,
) : WorkerResultWithRowCount {
    override val rowCount: Long by lazy {
        when {
            data.results.values.isEmpty() -> 0L
            else -> data.results.values[0][0].unsafeCast<Double>().toLong()
        }
    }

    override val result: WorkerResult = data.results
}

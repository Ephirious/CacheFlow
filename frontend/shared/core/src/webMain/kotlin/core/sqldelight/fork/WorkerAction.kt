package core.sqldelight.fork

internal sealed interface WorkerAction

@Suppress("NOTHING_TO_INLINE", "FunctionName", "RedundantSuppression")
internal inline fun WorkerAction(value: String) = value.unsafeCast<WorkerAction>()

internal object WorkerActions {
    /**
     * Execute a SQL statement.
     */
    inline val exec: WorkerAction get() = WorkerAction("exec")

    /**
     * Begin a transaction in the underlying SQL implementation.
     */
    inline val beginTransaction: WorkerAction get() = WorkerAction("begin_transaction")

    /**
     * End or commit a transaction in the underlying SQL implementation.
     */
    inline val endTransaction: WorkerAction get() = WorkerAction("end_transaction")

    /**
     * Roll back a transaction in the underlying SQL implementation.
     */
    inline val rollbackTransaction: WorkerAction get() = WorkerAction("rollback_transaction")
}

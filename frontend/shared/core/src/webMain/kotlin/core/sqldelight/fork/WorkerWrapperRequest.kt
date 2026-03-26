package core.sqldelight.fork

/**
 * Messages sent by the SQLDelight driver to the worker.
 */
internal data class WorkerWrapperRequest(
    /**
     * A unique identifier used to identify responses to this message
     * @see WorkerResponse.id
     */
    val id: String,
    /**
     * The action that the worker should run.
     * @see WorkerAction
     */
    val action: WorkerAction,
    /**
     * The SQL to execute
     */
    var sql: String?,

    /**
     * SQL parameters to bind to the given [sql]
     */
    var statement: WorkerSqlPreparedStatement?,
)

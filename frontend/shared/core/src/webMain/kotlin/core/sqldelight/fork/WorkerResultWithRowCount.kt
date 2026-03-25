package core.sqldelight.fork

internal interface WorkerResultWithRowCount {
  val result: WorkerResult
  val rowCount: Long
}

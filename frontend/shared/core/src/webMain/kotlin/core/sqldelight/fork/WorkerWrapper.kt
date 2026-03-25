package core.sqldelight.fork

import app.cash.sqldelight.driver.worker.WebWorkerException
import app.cash.sqldelight.driver.worker.expected.Worker
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.BroadcastChannel
import org.w3c.dom.MessageEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class WorkerWrapper(
    private val worker: Worker,
    private val onDbUpdated: (dynamic) -> Unit,
) {

    private val syncChannel = BroadcastChannel("sqlite_sync_channel")
    private val pendingIds = mutableSetOf<String>()

    init {
        syncChannel.onmessage = { event ->
            val data = event.data.asDynamic()
            val requestId = data.id as? String

            if (requestId != null && requestId !in pendingIds) {
                onDbUpdated(data)
            }
        }
    }

    suspend fun execute(
        request: WorkerWrapperRequest,
    ): WorkerResultWithRowCount {
        return suspendCancellableCoroutine { continuation ->
            pendingIds.add(request.id)
            val messageListener = object : EventListener {
                override fun handleEvent(event: Event) {
                    val data = event.unsafeCast<MessageEvent>().data.unsafeCast<JsWorkerResponse>()
                    if (data.id == request.id) {
                        worker.removeEventListener("message", this)
                        pendingIds.remove(request.id)
                        if (data.error != null) {
                            continuation.resumeWithException(
                                WebWorkerException(
                                    JSON.stringify(
                                        data.error,
                                        arrayOf("message", "arguments", "type", "name"),
                                    ),
                                ),
                            )
                        } else {
                            continuation.resume(
                                JsWorkerResultWithRowCount(data),
                            )
                        }
                    }
                }
            }

            val errorListener = object : EventListener {
                override fun handleEvent(event: Event) {
                    worker.removeEventListener("error", this)
                    pendingIds.remove(request.id)
                    continuation.resumeWithException(
                        WebWorkerException(
                            JSON.stringify(
                                event,
                                arrayOf("message", "arguments", "type", "name"),
                            ) + js("Object.entries(event)"),
                        ),
                    )
                }
            }

            worker.addEventListener("message", messageListener)
            worker.addEventListener("error", errorListener)

            val messageObject = buildRequest {
                this.id = request.id
                this.action = request.action
                this.sql = request.sql
                this.params = request.statement?.parameters?.toTypedArray()
            }

            worker.postMessage(messageObject)

            continuation.invokeOnCancellation {
                worker.removeEventListener("message", messageListener)
                worker.removeEventListener("error", errorListener)
                pendingIds.remove(request.id)
            }
        }
    }

    fun terminate() {
        worker.terminate()
        syncChannel.close()
    }
}

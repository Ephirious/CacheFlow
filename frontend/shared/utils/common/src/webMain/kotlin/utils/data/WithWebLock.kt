package utils.data

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.completeWith
import kotlinx.coroutines.promise
import kotlin.js.Promise

suspend fun <T> withWebLock(
    scope: CoroutineScope,
    lockName: String = "db",
    skipIfLocked: Boolean = true,
    block: suspend () -> T
): T? {
    val navigator = js("globalThis.navigator")

    if (navigator.locks == null) {
        println("Web Locks API not supported, executing without lock")
        return block()
    }

    val deferred = CompletableDeferred<T?>()

    val options = js("{}")
    options["ifAvailable"] = skipIfLocked

    navigator.locks.request(lockName, options) { lock ->
        if (lock == null) {
            deferred.complete(null)
            return@request Promise.resolve(null)
        }

        val promise = scope.promise {
            try {
                val result = block()
                deferred.complete(result)
            } catch (e: Throwable) {
                deferred.completeWith(Result.failure(e))
            }
        }
        promise
    }.catch { err ->
        if (!deferred.isCompleted) {
            deferred.completeWith(Result.failure(err as Throwable))
        }
    }

    return deferred.await()
}
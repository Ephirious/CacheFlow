package utils.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.js.Promise

suspend fun <T> withWebLock(scope: CoroutineScope, lockName: String = "db", block: suspend () -> T): T {
    val navigator = js("globalThis.navigator")

    if (navigator.locks == null) {
        println("Web Locks API not supported, executing without lock")
        return block()
    }

    return suspendCancellableCoroutine { continuation ->
        navigator.locks.request(lockName) { _ ->
            val promise = Promise { resolve, reject ->
                scope.promise {
                    try {
                        val result = block()
                        resolve(result)
                    } catch (e: Throwable) {
                        reject(e)
                    }
                }
            }
            promise
        }.then { result ->
            continuation.resume(result as T)
        }.catch { err ->
            continuation.resumeWith(Result.failure(err))
        }
    }
}
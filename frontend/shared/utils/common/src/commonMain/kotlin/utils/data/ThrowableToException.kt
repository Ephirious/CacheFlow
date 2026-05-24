package utils.data

import kotlin.coroutines.cancellation.CancellationException

inline fun <R> throwableToException(block: () -> R): R {
    return try {
        block()
    } catch (e: Throwable) {
        if (e is CancellationException) throw e

        throw (e as? Exception ?: RuntimeException(e.message, e))
    }
}
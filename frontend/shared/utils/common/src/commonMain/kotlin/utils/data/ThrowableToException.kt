package utils.data

import localization.HardCodedServerError
import localization.localz
import kotlin.coroutines.cancellation.CancellationException

inline fun <R> throwableToException(block: () -> R): R {
    return try {
        block()
    } catch (e: Throwable) {
        if (e is CancellationException) throw e

        // I'm so sorry, but it's MVP...
        e.message?.let { msg ->
            if (msg.contains("Email already registered")) {
                throw RuntimeException(localz.by(HardCodedServerError.EmailAlreadyInUse))
            } else if (msg.contains("Invalid login or password")) {
                throw RuntimeException(localz.by(HardCodedServerError.WrongLoginOrPassword))
            } else if (msg.contains("Fail to fetch")) {
                throw RuntimeException(localz.by(HardCodedServerError.FailToFetch))
            }
        }


        throw (e as? Exception ?: RuntimeException(e.message, e))
    }
}
package utils.data

import kotlinx.coroutines.CoroutineScope

actual suspend fun <T> withWebLock(
    scope: CoroutineScope,
    lockName: String,
    skipIfLocked: Boolean,
    block: suspend () -> T
): T? {
    TODO("Not yet implemented")
}
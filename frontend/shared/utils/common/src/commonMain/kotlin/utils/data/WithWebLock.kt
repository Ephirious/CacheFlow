package utils.data

import kotlinx.coroutines.CoroutineScope

expect suspend fun <T> withWebLock(
    scope: CoroutineScope,
    lockName: String = "db",
    skipIfLocked: Boolean = true,
    block: suspend () -> T
): T?
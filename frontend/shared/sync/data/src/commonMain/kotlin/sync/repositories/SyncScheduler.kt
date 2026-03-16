package sync.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.seconds

class SyncScheduler(
    scope: CoroutineScope,
    dbFlows: List<Flow<Any>>
) {

    companion object {
        private val IDLE_DEBOUNCE = 2.seconds
    }

    private val trigger = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1
    )

    @OptIn(FlowPreview::class)
    val debouncedSyncEvents: Flow<Unit> = merge(
        trigger,
        combine(dbFlows) { }
    ).debounce(IDLE_DEBOUNCE)
        .shareIn(scope, SharingStarted.WhileSubscribed())

    suspend fun schedule() = trigger.emit(Unit)
}
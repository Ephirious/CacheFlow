package sync.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable


@Serializable
data class SyncData(
    val transactions: List<SampleDBData>,
    val categories: List<SampleDBData>,
    val accounts: List<SampleDBData>,
)

@Serializable
data class SampleDBData(
    val timestamp: String,
    val isSynced: Boolean
)

fun createMockFlow(name: String): Flow<List<SampleDBData>> = flow {
//    while (true) {
//        val delaySec = Random.nextLong(5, 20)
//        delay(delaySec * 1000)
//
//        println("[Mock] 🕒 Поток '$name' сгенерировал обновление ($delaySec sec)")
//        emit(listOf(SampleDBData("timestamp-${Clock.System.now().epochSeconds}", false)))
//    }
}

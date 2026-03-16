package sync.repositories

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
sealed interface AppServiceMessage {
    @Serializable
    @SerialName("status_changed")
    data class StatusChanged(val status: SyncStatus) : AppServiceMessage

    @Serializable
    @SerialName("db_updated")
    data object DataUpdated : AppServiceMessage
}
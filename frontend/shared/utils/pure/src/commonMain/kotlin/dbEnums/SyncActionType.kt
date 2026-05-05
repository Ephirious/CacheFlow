package dbEnums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SyncActionType {
    @SerialName("create") CREATE,
    @SerialName("update") UPDATE,
    @SerialName("delete") DELETE,
}

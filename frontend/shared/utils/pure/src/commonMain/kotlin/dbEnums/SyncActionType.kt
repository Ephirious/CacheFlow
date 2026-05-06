package dbEnums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("EnumEntryName")
enum class SyncActionType {
    @SerialName("create") create,
    @SerialName("update") update,
    @SerialName("delete") delete,
}
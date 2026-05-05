package dbEnums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SyncTableType {
    @SerialName("accounts") ACCOUNTS,
    @SerialName("categories") CATEGORIES,
    @SerialName("transfer") TRANSFER,
    @SerialName("operations") OPERATIONS
}

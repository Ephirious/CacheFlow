package dbEnums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("EnumEntryName")
enum class SyncTableType {
    @SerialName("accounts") accounts,
    @SerialName("categories") categories,
    @SerialName("transfer") transfer,
    @SerialName("operations") operations
}

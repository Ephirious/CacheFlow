package sync.cloud.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
sealed interface RecordDTO

@Serializable
data class AccountRecordDTO(
    val id: String,
    val name: String,
    val color: String,
    val funds: String
) : RecordDTO

@Serializable
data class CategoryRecordDTO(
    val id: String,
    val name: String,
) : RecordDTO

@Serializable
data class TransferRecordDTO(
    val id: String,
    val accountFromId: String,
    val accountToId: String
) : RecordDTO

@Serializable
data class OperationRecordDTO(
    val id: String,
    val accountUuid: String,
    val transferId: String?,
    val categoryId: String?,
    val amount: String,
    val date: String,
    val notes: String
) : RecordDTO


@Serializable
enum class SyncActionType {
    @SerialName("create") CREATE,
    @SerialName("update") UPDATE,
    @SerialName("delete") DELETE,
}

@Serializable
enum class SyncTableType {
    @SerialName("accounts") ACCOUNTS,
    @SerialName("categories") CATEGORIES,
    @SerialName("transfer") TRANSFER,
    @SerialName("operations") OPERATIONS
}

@Serializable
data class SyncOperationDTO(
    val id: String,
    @SerialName("processing_id") val processingId: String,
    @SerialName("created_at") val createdAt: String,
    val action: SyncActionType,
    @SerialName("table_type") val tableType: SyncTableType,
    @SerialName("field_to_update") val fieldToUpdate: String?,
    @SerialName("value_to_update") val valueToUpdate: String?,
    @SerialName("record_to_create") val recordToCreate: RecordDTO? = null,
)

@Serializable
data class SyncRequest(
    @SerialName("last_sync_date") val lastSyncDate: String,
    @SerialName("sync_ops") val operations: List<SyncOperationDTO>, // Поправил на sync_ops как в Python
)

@Serializable
data class UpdateState(
    @SerialName("table_type") val tableType: SyncTableType,
    val record: JsonElement, // Сначала берем как сырой JSON
    @SerialName("updated_at") val updatedAt: String,
) {
    fun getTypedRecord(json: Json): RecordDTO {
        return when (tableType) {
            SyncTableType.ACCOUNTS -> json.decodeFromJsonElement<AccountRecordDTO>(record)
            SyncTableType.CATEGORIES -> json.decodeFromJsonElement<CategoryRecordDTO>(record)
            SyncTableType.TRANSFER -> json.decodeFromJsonElement<TransferRecordDTO>(record)
            SyncTableType.OPERATIONS -> json.decodeFromJsonElement<OperationRecordDTO>(record)
        }
    }
}

@Serializable
data class DeleteOperation(
    @SerialName("table_type") val tableType: SyncTableType,
    val id: String

)

@Serializable
data class SyncResponse(
    @SerialName("last_sync_date") val lastSyncDate: String,
    @SerialName("accepted_ids") val acceptedIds: List<String>,
    @SerialName("delete_operations") val deleteOperations: List<DeleteOperation>,
    @SerialName("update_state") val updateState: List<UpdateState>
)
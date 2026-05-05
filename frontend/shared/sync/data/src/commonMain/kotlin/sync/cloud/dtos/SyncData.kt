package sync.cloud.dtos

import dbEnums.CategoryType
import dbEnums.SyncActionType
import dbEnums.SyncTableType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
sealed interface RecordCreateDTO

@Serializable
sealed interface RecordOutDTO

@Serializable
data class AccountRecordCreateDTO(
    val id: String,
    val name: String,
    val color: String,
) : RecordCreateDTO


@Serializable
data class AccountOutDTO(
    val id: String,
    val name: String,
    val color: String,
    val funds: String
) : RecordOutDTO

@Serializable
data class CategoryRecordCreateDTO(
    val id: String,
    val name: String,
    val emoji: String,
    val type: CategoryType
) : RecordCreateDTO, RecordOutDTO

@Serializable
data class TransferRecordCreateDTO(
    val id: String,
    val accountFromId: String,
    val accountToId: String
) : RecordCreateDTO, RecordOutDTO

@Serializable
data class OperationRecordCreateDTO(
    val id: String,
    val accountUuid: String,
    val transferId: String?,
    val categoryId: String?,
    val amount: String,
    val date: String,
    val notes: String
) : RecordCreateDTO, RecordOutDTO




@Serializable
data class SyncOperationDTO(
    val id: String,
    @SerialName("processing_id") val processingId: String,
    @SerialName("created_at") val createdAt: String,
    val action: SyncActionType,
    @SerialName("table_type") val tableType: SyncTableType,
    @SerialName("field_to_update") val fieldToUpdate: String?,
    @SerialName("value_to_update") val valueToUpdate: String?,
    @SerialName("record_to_create") val recordToCreate: RecordCreateDTO? = null,
)

@Serializable
data class SyncRequest(
    @SerialName("last_sync_date") val lastSyncDate: String,
    @SerialName("sync_ops") val operations: List<SyncOperationDTO>,
)

@Serializable
data class UpdateState(
    @SerialName("table_type") val tableType: SyncTableType,
    val record: JsonElement,
    @SerialName("updated_at") val updatedAt: String,
) {
    fun getTypedRecord(): RecordOutDTO {
        return when (tableType) {
            SyncTableType.ACCOUNTS -> Json.decodeFromJsonElement<AccountOutDTO>(record)
            SyncTableType.CATEGORIES -> Json.decodeFromJsonElement<CategoryRecordCreateDTO>(record)
            SyncTableType.TRANSFER -> Json.decodeFromJsonElement<TransferRecordCreateDTO>(record)
            SyncTableType.OPERATIONS -> Json.decodeFromJsonElement<OperationRecordCreateDTO>(record)
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
package sync.mappers

import data.SyncOperationEntity
import kotlinx.serialization.json.Json
import sync.cloud.dtos.*

fun SyncOperationEntity.toDTO(json: Json): SyncOperationDTO {
    val rawValue = this.value_to_update

    val recordObj: RecordDTO? = if (this.action == SyncActionType.CREATE && rawValue != null) {
        when (this.table_type) {
            SyncTableType.ACCOUNTS -> json.decodeFromString<AccountRecordDTO>(rawValue)
            SyncTableType.CATEGORIES -> json.decodeFromString<CategoryRecordDTO>(rawValue)
            SyncTableType.TRANSFER -> json.decodeFromString<TransferRecordDTO>(rawValue)
            SyncTableType.OPERATIONS -> json.decodeFromString<OperationRecordDTO>(rawValue)
            else -> null
        }
    } else {
        null
    }

    return SyncOperationDTO(
        id = this.id,
        processingId = this.processing_id,
        createdAt = this.created_at,
        action = this.action,
        tableType = this.table_type,
        fieldToUpdate = this.field_to_update,
        valueToUpdate = if (this.action == SyncActionType.UPDATE) rawValue else null,
        recordToCreate = recordObj
    )
}
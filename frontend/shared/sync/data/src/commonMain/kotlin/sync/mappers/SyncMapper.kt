package sync.mappers

import data.GetSyncQueue
import sync.cloud.dtos.*

fun mapSyncQueueRow(row: GetSyncQueue): SyncOperationDTO {
    val record: RecordDTO? = when (row.action) {
        SyncActionType.CREATE -> when (row.table_type) {
            SyncTableType.ACCOUNTS -> AccountRecordDTO(
                id = row.processing_id,
                name = row.acc_name ?: "",
                color = row.acc_color ?: "",
                funds = (row.acc_funds ?: 0).toString()
            )
            SyncTableType.CATEGORIES -> CategoryRecordDTO(
                id = row.processing_id,
                name = row.cat_name ?: "",
                row.cat_emoji ?: ""

            )
            SyncTableType.OPERATIONS -> OperationRecordDTO(
                id = row.processing_id,
                accountUuid = row.oper_account_uuid ?: "",
                amount = (row.oper_amount ?: "0").toString(),
                date = (row.oper_date ?: "").toString(),
                notes = row.oper_notes ?: "",
                categoryId = row.oper_category_uuid,
                transferId = row.oper_transfer_id
            )
            SyncTableType.TRANSFER -> TransferRecordDTO(
                id = row.processing_id,
                accountFromId = row.tran_account_from_id ?: "",
                accountToId = row.tran_account_to_id ?: ""
            )

            else -> {null}
        }
        else -> null
    }

    return SyncOperationDTO(
        id = row.id,
        processingId = row.processing_id,
        action = row.action,
        tableType = row.table_type,
        fieldToUpdate = row.field_to_update,
        valueToUpdate = row.value_to_update,
        recordToCreate = record,
        createdAt = row.created_at
    )
}
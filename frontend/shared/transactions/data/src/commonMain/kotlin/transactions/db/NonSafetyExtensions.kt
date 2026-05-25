package transactions.db

import utils.types.BigDecimal
import kotlin.time.Instant

suspend fun TransactionsDatabaseDataSource.hardDeleteTransaction(id: String) {
    transactionsQueries.delete(id)
}

suspend fun TransactionsDatabaseDataSource.hardDeleteTransfer(id: String) {
    transfersQueries.delete(id)
}

suspend fun TransactionsDatabaseDataSource.badInsertTransaction(
    id: String,
    accountUuid: String,
    transferId: String?,
    categoryId: String?,
    amount: String,
    date: String,
    notes: String
) {
    transactionsQueries.upsert(
        id = id,
        account_uuid = accountUuid,
        category_uuid = categoryId,
        transfer_id = transferId,
        amount = BigDecimal(amount),
        date = Instant.parse(date),
        notes = notes
    )
}

suspend fun TransactionsDatabaseDataSource.badInsertTransfer(
    id: String,
    accountFromId: String,
    accountToId: String
) {
    transfersQueries.upsert(
        id = id,
        account_from_id = accountFromId,
        account_to_id = accountToId,
    )
}
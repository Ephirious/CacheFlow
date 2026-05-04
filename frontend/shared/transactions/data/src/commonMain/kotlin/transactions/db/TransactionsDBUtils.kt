package transactions.db

import data.OperationsQueries
import data.TransfersQueries
import transactions.models.Transaction
import transactions.models.TransactionType
import utils.toInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal suspend fun OperationsQueries.upsertFrom(
    transaction: Transaction,
    transferId: String?,
): String {
    val id = transaction.id ?: Uuid.generateV7().toString()

    val categoryId = when (val t = transaction.type) {
        is TransactionType.Income -> t.category.id
        is TransactionType.Outcome -> t.category.id
        is TransactionType.Transfer -> null
    }

    this.upsert(
        id = id,
        account_uuid = transaction.account.id,
        category_uuid = categoryId,
        transfer_id = transferId,
        amount = transaction.value,
        date = transaction.date.toInstant(),
        notes = transaction.note
    )

    return id
}


@OptIn(ExperimentalUuidApi::class)
internal suspend fun TransfersQueries.upsertFrom(transaction: Transaction, transferId: String?): String? {
    if (transaction.type !is TransactionType.Transfer) return null
    val id = transferId ?: Uuid.generateV7().toString()
    with((transaction.type as TransactionType.Transfer)) {
        this@upsertFrom.upsert(id = id, account_from_id = from.id, account_to_id = to.id)
    }
    return id
}

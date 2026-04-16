package transactions.mappers

import data.SelectAllWithAccountAndCategory
import editors.models.Account
import editors.models.Category
import transactions.models.Transaction
import transactions.models.TransactionType
import utils.toLocalDate
import utils.types.BigDecimal
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal fun SelectAllWithAccountAndCategory.toDomain(): Transaction {
    val currentAccount = Account(
        id = this.acc_id,
        title = this.acc_name,
        balance = this.acc_funds
    )

    val category = if (this.cat_id != null && this.cat_name != null) {
        Category(id = this.cat_id!!, name = this.cat_name!!)
    } else {
        Category.Unknown
    }

    val transactionType = when {
        // Transfer
        this.transfer_id != null -> {
            val targetAccount = Account(
                id = this.target_acc_id ?: Uuid.generateV7().toString(),
                title = this.target_acc_name ?: "Неизвестный счёт",
                balance = this.target_acc_funds ?: BigDecimal.ZERO,
            )
            TransactionType.Transfer(from = currentAccount, to = targetAccount)
        }
        // Income
        this.amount.isPositive -> {
            TransactionType.Income(category = category)
        }
        // Outcome
        else -> {
            TransactionType.Outcome(category = category)
        }
    }

    return Transaction(
        id = this.id,
        value = this.amount,
        type = transactionType,
        account = currentAccount,
        note = notes,
        date = date.toLocalDate(),
    )
}

internal fun List<SelectAllWithAccountAndCategory>.listToDomain() = this.map { it.toDomain() }

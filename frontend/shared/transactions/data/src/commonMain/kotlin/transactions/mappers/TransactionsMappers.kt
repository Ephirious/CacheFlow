package transactions.mappers

import data.SelectAllFiltered
import data.SelectAllWithAccountAndCategory
import data.SelectPrimaryWithAccountAndCategoryById
import dbEnums.CategoryType
import editors.models.Account
import editors.models.Category
import transactions.models.Transaction
import transactions.models.TransactionType
import utils.toLocalDate
import utils.types.BigDecimal
import utils.types.HexColor
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


internal data class TransactionWithAccountAndCategory
    (
    val id: String,
    val amount: BigDecimal,
    val date: Instant,
    val notes: String,
    val created_at: Instant,
    val acc_id: String,
    val acc_name: String,
    val acc_funds: BigDecimal,
    val acc_color: String,
    val cat_id: String?,
    val cat_name: String?,
    val cat_emoji: String?,
    val cat_type: CategoryType?,
    val transfer_id: String?,
    val target_acc_id: String?,
    val target_acc_name: String?,
    val target_acc_funds: BigDecimal?,
    val target_acc_color: String?,
)


fun SelectAllWithAccountAndCategory.toDomain(): Transaction =
    TransactionWithAccountAndCategory(
        id = id,
        amount = amount,
        date = date,
        notes = notes,
        created_at = created_at,
        acc_id = acc_id,
        acc_name = acc_name,
        acc_funds = acc_funds,
        acc_color = acc_color,
        cat_id = cat_id,
        cat_name = cat_name,
        cat_emoji = cat_emoji,
        cat_type = cat_type,
        transfer_id = transfer_id,
        target_acc_id = target_acc_id,
        target_acc_name = target_acc_name,
        target_acc_funds = target_acc_funds,
        target_acc_color = target_acc_color,
    ).toDomain()

fun SelectPrimaryWithAccountAndCategoryById.toDomain(): Transaction =
    TransactionWithAccountAndCategory(
        id = id,
        amount = amount,
        date = date,
        notes = notes,
        created_at = created_at,
        acc_id = acc_id,
        acc_name = acc_name,
        acc_funds = acc_funds,
        acc_color = acc_color,
        cat_id = cat_id,
        cat_name = cat_name,
        cat_emoji = cat_emoji,
        cat_type = cat_type,
        transfer_id = transfer_id,
        target_acc_id = target_acc_id,
        target_acc_name = target_acc_name,
        target_acc_funds = target_acc_funds,
        target_acc_color = target_acc_color,
    ).toDomain()

@OptIn(ExperimentalUuidApi::class)
internal fun TransactionWithAccountAndCategory.toDomain(): Transaction {
    val currentAccount = Account(
        id = this.acc_id,
        title = this.acc_name,
        balance = this.acc_funds,
        color = HexColor(this.acc_color)
    )

    val category = if (this.cat_id != null && this.cat_name != null) {
        Category(id = this.cat_id, name = this.cat_name, emoji = this.cat_emoji!!, type = this.cat_type!!)
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
                color = this.target_acc_color?.let { HexColor(it) } ?: HexColor("#FF0000")
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


@OptIn(ExperimentalUuidApi::class)
internal fun SelectAllFiltered.toDomain(): Transaction {
    val currentAccount = Account(
        id = this.acc_id,
        title = this.acc_name,
        balance = this.acc_funds,
        color = HexColor(this.acc_color)
    )

    val category = if (this.cat_id != null && this.cat_name != null) {
        Category(id = this.cat_id!!, name = this.cat_name!!, emoji = this.cat_emoji!!, type = this.cat_type!!)
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
                color = this.target_acc_color?.let { HexColor(it) } ?: HexColor("#FF0000")
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
internal fun List<SelectAllFiltered>.listToDomain() = this.map { it.toDomain() }

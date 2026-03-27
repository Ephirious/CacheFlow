package transactions.models

import editors.models.Account
import editors.models.Category
import kotlinx.datetime.LocalDate
import utils.types.BigDecimal
import kotlin.js.JsExport

@JsExport
data class Transaction(
    val id: String,
    val value: BigDecimal,
    val type: TransactionType,
    val account: Account,
    val note: String,
    val date: LocalDate,
)

@JsExport
sealed class TransactionType {
    data class Income(val category: Category) : TransactionType()
    data class Outcome(val category: Category) : TransactionType()
    data class Transfer(val from: Account, val to: Account) : TransactionType()
}



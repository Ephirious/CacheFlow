package transactions.models

import editors.models.Account
import editors.models.Category
import utils.types.BigDecimal
import kotlin.js.JsExport
import kotlin.time.Instant

@JsExport
data class Transaction(
    val id: String,
    val value: BigDecimal,
    val title: String,
    val type: TransactionType,
    val account: Account,
    val note: String,
    val date: Instant,
)

@JsExport
sealed class TransactionType {
    data class Income(val category: Category) : TransactionType()
    data class Outcome(val category: Category) : TransactionType()
    data class Transfer(val from: Account, val to: Account) : TransactionType()
}



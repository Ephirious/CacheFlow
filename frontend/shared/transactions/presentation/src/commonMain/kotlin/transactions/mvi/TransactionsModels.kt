package transactions.mvi

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import transactions.models.Transaction
import kotlin.js.JsExport


@JsExport
data class TransactionsState(
    val transactions: List<Transaction>,
    val expandedTransactions: List<String> = emptyList(),
) : MVIState

@JsExport
sealed class TransactionsIntent : MVIIntent {
    data class TransactionClicked(val transactionId: String) : TransactionsIntent()
}
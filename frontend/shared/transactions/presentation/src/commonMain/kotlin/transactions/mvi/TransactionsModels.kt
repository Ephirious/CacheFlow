package transactions.mvi

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState


@JsExport
data class TransactionsState(
    val transactions: List<Any>, // TODO
    val expandedTransactions: List<String> = emptyList(),
) : MVIState

@JsExport
sealed class TransactionsIntent : MVIIntent {
    data class TransactionClicked(val transactionId: String) : TransactionsIntent()
}
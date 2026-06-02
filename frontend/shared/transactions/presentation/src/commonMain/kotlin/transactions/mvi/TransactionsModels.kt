package transactions.mvi

import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import transactions.models.Transaction
import transactions.models.TransactionFilters
import kotlin.js.JsExport


@JsExport
data class TransactionsState(
    val transactions: List<Transaction>,
    val filters: TransactionFilters = TransactionFilters(),
    val limit: Long = 20L,
    val isEndOfList: Boolean = false,
    val isLoadingMore: Boolean = false
) : MVIState

@JsExport
sealed class TransactionsIntent : MVIIntent {
    data object LoadMore : TransactionsIntent()
    data class FiltersApplied(val filters: TransactionFilters) : TransactionsIntent()
    data object CloseFilters : TransactionsIntent()
}


@JsExport
sealed class TransactionsAction : MVIAction {
    data object HideFilters : TransactionsAction()
}
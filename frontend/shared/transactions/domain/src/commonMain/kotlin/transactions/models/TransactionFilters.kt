package transactions.models

import kotlin.js.JsExport
import kotlin.time.Instant

@JsExport
data class TransactionFilters(
    val allowIncome: Boolean = false,
    val allowOutcome: Boolean = false,
    val allowTransfer: Boolean = false,
    val noteQuery: String? = null,
    val dateFrom: Instant? = null,
    val dateTo: Instant? = null,
    val categoryIds: List<String> = emptyList(),
    val accountIds: List<String> = emptyList()
) {
    val isEmpty: Boolean
        get() = !allowIncome && !allowOutcome && !allowTransfer &&
                noteQuery.isNullOrBlank() && dateFrom == null && dateTo == null &&
                categoryIds.isEmpty() && accountIds.isEmpty()
}
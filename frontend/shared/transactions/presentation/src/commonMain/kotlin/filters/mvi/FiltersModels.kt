package filters.mvi

import dbEnums.TransactionTypeEnum
import editors.models.Account
import editors.models.Category
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import transactions.models.TransactionFilters
import kotlin.js.JsExport

@JsExport
data class FiltersState(
    val currentFilters: TransactionFilters = TransactionFilters(),
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList()
) : MVIState

@JsExport
sealed class FiltersIntent : MVIIntent {

    data class ToggleTransactionType(val type: TransactionTypeEnum) : FiltersIntent()
    data class ToggleCategory(val categoryId: String) : FiltersIntent()
    data class ToggleAccount(val accountId: String) : FiltersIntent()
    data class UpdateNote(val query: String) : FiltersIntent()
    data class UpdateDateFrom(val date: String?) : FiltersIntent()
    data class UpdateDateTo(val date: String?) : FiltersIntent()

    data class UpdateFilters(val filters: TransactionFilters) : FiltersIntent()
    data object ResetFilters : FiltersIntent()
    data object ApplyClicked : FiltersIntent()
    data object CloseClicked : FiltersIntent()
}
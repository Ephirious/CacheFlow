package summary.mvi

import editors.models.Account
import pro.respawn.flowmvi.api.MVIState
import utils.types.BigDecimal
import kotlin.js.JsExport

@JsExport
data class SummaryState(
    val overallBalance: BigDecimal,
    val profitPercentage: BigDecimal,
    val accounts: List<Account>
) : MVIState
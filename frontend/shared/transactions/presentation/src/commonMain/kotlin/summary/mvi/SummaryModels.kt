package summary.mvi

import pro.respawn.flowmvi.api.MVIState
import utils.BigDecimal

@JsExport
data class SummaryState(
    val overallBalance: BigDecimal,
    val profitPercentage: BigDecimal,
    val accounts: List<Any> // TODO
) : MVIState
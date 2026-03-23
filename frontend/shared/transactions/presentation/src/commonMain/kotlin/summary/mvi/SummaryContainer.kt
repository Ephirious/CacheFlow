package summary.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import utils.BigDecimal
import utils.orUnknown
import utils.presentation.flowMVI.fastConfig

private typealias Ctx = PipelineContext<SummaryState, Nothing, Nothing>


class SummaryContainer(
    private val throwErrorToParent: (() -> String) -> Unit
) : Container<SummaryState, Nothing, Nothing> {
    override val store: Store<SummaryState, Nothing, Nothing> =
        store(
            // TODO
            initial = SummaryState(
                overallBalance = BigDecimal(0),
                profitPercentage = BigDecimal(0),
                accounts = emptyList(),
            ),
        ) {
            fastConfig(
                name = "Summary",
                resetOnStop = true,
                doOnRecover = { throwErrorToParent { it.message.orUnknown }; this }
            )

            // subscribe on flow here TODO
        }
}
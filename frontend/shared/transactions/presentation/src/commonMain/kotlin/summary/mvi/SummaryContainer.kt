package summary.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.resetStateOnStop
import utils.AppConfig
import utils.BigDecimal

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
            configure {
                name = "Summary"
                debuggable = AppConfig.isDebuggable
            }
            enableLogging()
            resetStateOnStop()

            recover {
                throwErrorToParent { it.message ?: "unknown error!" }
                null
            }

            // subscribe on flow here TODO
        }
}
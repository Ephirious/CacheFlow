package transactions.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.enableLogging
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.resetStateOnStop
import utils.AppConfig

private typealias Ctx = PipelineContext<TransactionsState, TransactionsIntent, Nothing>


class TransactionsContainer(
    private val throwErrorToParent: (() -> String) -> Unit
) : Container<TransactionsState, TransactionsIntent, Nothing> {
    override val store: Store<TransactionsState, TransactionsIntent, Nothing> =
        store(
            // TODO
            initial = TransactionsState(
                transactions = listOf()
            ),
        ) {
            configure {
                name = "Transactions"
                debuggable = AppConfig.isDebuggable
            }
            enableLogging()
            resetStateOnStop()

            recover {
                throwErrorToParent { it.message ?: "unknown error!" }
                null
            }

            // subscribe on flow here TODO

            reduce { intent ->
                when (intent) {
                    is TransactionsIntent.TransactionClicked -> TODO()
                }
            }
        }
}
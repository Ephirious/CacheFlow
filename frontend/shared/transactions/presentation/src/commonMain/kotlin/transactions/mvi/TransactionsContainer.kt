package transactions.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import utils.orUnknown
import utils.presentation.flowMVI.fastConfig

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
            fastConfig(
                name = "Transactions",
                resetOnStop = true,
                doOnRecover = { throwErrorToParent { it.message.orUnknown }; this }
            )

            // subscribe on flow here TODO

            reduce { intent ->
                when (intent) {
                    is TransactionsIntent.TransactionClicked -> TODO()
                }
            }
        }
}
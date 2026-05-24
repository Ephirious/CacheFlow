package transactions.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.whileSubscribed
import transactions.usecases.GetTransactionsFlowUseCase
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.observe

private typealias Ctx = PipelineContext<TransactionsState, TransactionsIntent, Nothing>

private enum class Jobs {
    ObserveTransactions
}

class TransactionsContainer(
    private val throwErrorToParent: (() -> String) -> Unit,
    private val getTransactionsFlowUseCase: GetTransactionsFlowUseCase,
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
                doOnRecover = { state, e -> throwErrorToParent { e.message.orUnknown }; state }
            )

            val jobs = JobManager<Jobs>()

            whileSubscribed {
                observeTransactions(jobs)
            }

            customReduce { intent ->
                when (intent) {
                    is TransactionsIntent.TransactionClicked -> TODO()
                }
            }
        }


    private fun Ctx.observeTransactions(jobs: JobManager<Jobs>) {

        observe(
            flow = getTransactionsFlowUseCase(accountId = null),
            jobs = jobs,
            key = Jobs.ObserveTransactions
        ) { transactions ->
            updateState { copy(transactions = transactions) }
        }
    }
}
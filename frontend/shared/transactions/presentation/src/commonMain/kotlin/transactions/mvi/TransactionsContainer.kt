package transactions.mvi

import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import transactions.usecases.GetTransactionsFlowUseCase
import utils.orUnknown
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.registerOrIgnore

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
                doOnRecover = { throwErrorToParent { it.message.orUnknown }; this }
            )

            val jobs = JobManager<Jobs>()

            whileSubscribed {
                observeTransactions(jobs)
            }

            reduce { intent ->
                when (intent) {
                    is TransactionsIntent.TransactionClicked -> TODO()
                }
            }
        }


    private fun Ctx.observeTransactions(jobs: JobManager<Jobs>) {
        launch {
            getTransactionsFlowUseCase().collect { transactions ->
                updateState { copy(transactions = transactions) }
            }
        }.registerOrIgnore(jobs, Jobs.ObserveTransactions)
    }
}
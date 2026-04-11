package summary.mvi

import editors.usecases.account.GetAccountsFlowUseCase
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.whileSubscribed
import utils.orUnknown
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.observe
import utils.types.BigDecimal

private typealias Ctx = PipelineContext<SummaryState, Nothing, Nothing>

private enum class Jobs {
    ObserveAccounts
}

class SummaryContainer(
    private val throwErrorToParent: (() -> String) -> Unit,
    private val getAccountsFlowUseCase: GetAccountsFlowUseCase,
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

            val jobs = JobManager<Jobs>()

            whileSubscribed {
                observeAccounts(jobs)
            }
        }

    private fun Ctx.observeAccounts(jobs: JobManager<Jobs>) {

        observe(
            flow = getAccountsFlowUseCase(),
            jobs = jobs,
            key = Jobs.ObserveAccounts
        ) { accounts ->
            var overallSum = BigDecimal.ZERO
            accounts.forEach { overallSum += it.balance }
            updateState { copy(accounts = accounts, overallBalance = overallSum) }
        }
    }
}
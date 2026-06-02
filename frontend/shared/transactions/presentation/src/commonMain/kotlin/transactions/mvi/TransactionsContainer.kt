package transactions.mvi

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.whileSubscribed
import transactions.models.TransactionFilters
import transactions.usecases.GetFilteredTransactionsFlowUseCase
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig

private typealias Ctx = PipelineContext<TransactionsState, TransactionsIntent, TransactionsAction>

class TransactionsContainer(
    private val throwErrorToParent: (() -> String) -> Unit,
    private val getFilteredTransactionsFlowUseCase: GetFilteredTransactionsFlowUseCase,
) : Container<TransactionsState, TransactionsIntent, TransactionsAction> {

    private val queryConfig = MutableStateFlow(Pair(TransactionFilters(), 20L))

    @OptIn(ExperimentalCoroutinesApi::class)
    override val store: Store<TransactionsState, TransactionsIntent, TransactionsAction> =
        store(
            initial = TransactionsState(
                transactions = listOf()
            ),
        ) {
            fastConfig(
                name = "Transactions",
                resetOnStop = true,
                doOnRecover = { state, e -> throwErrorToParent { e.message.orUnknown }; state }
            )

            whileSubscribed {
                queryConfig
                    .flatMapLatest { (filters, limit) ->
                        getFilteredTransactionsFlowUseCase(accountId = null, filters, limit)
                    }
                    .onEach { items ->
                        updateState {
                            copy(
                                transactions = items,
                                isEndOfList = items.size < limit,
                                isLoadingMore = false
                            )
                        }
                    }
                    .launchIn(this)
            }

            customReduce { intent ->
                when (intent) {
                    TransactionsIntent.LoadMore -> {
                        withState {
                            if (!isEndOfList && !isLoadingMore) {
                                val newLimit = limit + 20L
                                updateState { copy(limit = newLimit, isLoadingMore = true) }
                                queryConfig.value = filters to newLimit
                            }
                        }
                    }

                    is TransactionsIntent.FiltersApplied -> {
                        val newLimit = 20L
                        updateState { copy(filters = intent.filters, limit = newLimit) }
                        queryConfig.value = intent.filters to newLimit
                        action(TransactionsAction.HideFilters)
                    }

                    TransactionsIntent.CloseFilters -> action(TransactionsAction.HideFilters)
                }
            }
        }


}

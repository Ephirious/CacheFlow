package filters.mvi

import dbEnums.TransactionTypeEnum
import editors.usecases.account.GetAccountsFlowUseCase
import editors.usecases.category.GetCategoriesFlowUseCase
import kotlinx.datetime.LocalDate
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.whileSubscribed
import transactions.models.TransactionFilters
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.observe
import utils.toInstant


private typealias Ctx = PipelineContext<FiltersState, FiltersIntent, Nothing>

private enum class Jobs {
    ObserveAccounts, ObserveCategories
}

class FiltersContainer(
    initialFilters: TransactionFilters,
    private val getAccountsFlowUseCase: GetAccountsFlowUseCase,
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    private val onApply: (TransactionFilters) -> Unit,
    private val onClose: () -> Unit,
) : Container<FiltersState, FiltersIntent, Nothing> {
    override val store: Store<FiltersState, FiltersIntent, Nothing> =
        store(
            initial = FiltersState(currentFilters = initialFilters),
        ) {
            fastConfig(
                name = "Filters",
                resetOnStop = false,
                doOnRecover = null
            )

            val jobs = JobManager<Jobs>()

            whileSubscribed {
                observeAccounts(jobs)
                observeCategories(jobs)
            }


            customReduce { intent ->
                when (intent) {
                    is FiltersIntent.UpdateFilters -> updateState { copy(currentFilters = intent.filters) }
                    FiltersIntent.ResetFilters -> updateState { copy(currentFilters = TransactionFilters()) }
                    FiltersIntent.ApplyClicked -> {
                        withState { onApply(currentFilters) }
                    }

                    FiltersIntent.CloseClicked -> onClose()


                    is FiltersIntent.ToggleAccount -> updateState {
                        val ids = currentFilters.accountIds
                        val newIds = if (ids.contains(intent.accountId)) {
                            ids - intent.accountId
                        } else {
                            ids + intent.accountId
                        }
                        copy(currentFilters = currentFilters.copy(accountIds = newIds))
                    }

                    is FiltersIntent.ToggleCategory -> updateState {
                        val ids = currentFilters.categoryIds
                        val newIds = if (ids.contains(intent.categoryId)) {
                            ids - intent.categoryId
                        } else {
                            ids + intent.categoryId
                        }
                        copy(currentFilters = currentFilters.copy(categoryIds = newIds))
                    }

                    is FiltersIntent.ToggleTransactionType -> updateState {
                        val f = currentFilters
                        val newFilters = when (intent.type) {
                            TransactionTypeEnum.income -> f.copy(allowIncome = !f.allowIncome)
                            TransactionTypeEnum.outcome -> f.copy(allowOutcome = !f.allowOutcome)
                            TransactionTypeEnum.transfer -> f.copy(allowTransfer = !f.allowTransfer)
                        }
                        copy(currentFilters = newFilters)
                    }

                    is FiltersIntent.UpdateDateFrom ->
                        updateState {
                            copy(currentFilters = currentFilters.copy(dateFrom = intent.date.toInstant()))
                        }


                    is FiltersIntent.UpdateDateTo ->
                        updateState {
                            copy(currentFilters = currentFilters.copy(dateTo = intent.date.toInstant()))
                        }

                    is FiltersIntent.UpdateNote -> updateState {
                        copy(currentFilters = currentFilters.copy(noteQuery = intent.query))
                    }
                }
            }
        }

    private fun String?.toInstant() = this?.substringBefore('T')?.let {
        LocalDate.parse(it)
    }?.toInstant()

    private fun Ctx.observeAccounts(jobs: JobManager<Jobs>) {
        observe(
            flow = getAccountsFlowUseCase(), key = Jobs.ObserveAccounts, jobs = jobs
        ) { accounts ->
            updateState { copy(accounts = accounts) }
        }
    }

    private fun Ctx.observeCategories(jobs: JobManager<Jobs>) {
        observe(
            flow = getCategoriesFlowUseCase(), key = Jobs.ObserveCategories, jobs = jobs
        ) { categoriesLists ->
            updateState {
                copy(categories = categoriesLists.income + categoriesLists.outcome)
            }
        }
    }
}
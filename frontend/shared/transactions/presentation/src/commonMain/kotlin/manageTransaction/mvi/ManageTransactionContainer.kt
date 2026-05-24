package manageTransaction.mvi

import editors.usecases.account.GetAccountsFlowUseCase
import editors.usecases.category.GetCategoriesFlowUseCase
import manageTransaction.mvi.ManageTransactionType.*
import manageTransaction.mvi.base.manageTransactionBasePlugin
import manageTransaction.mvi.base.toFormState
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.withState
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.whileSubscribed
import transactions.usecases.DeleteTransactionUseCase
import transactions.usecases.GetTransactionUseCase
import transactions.usecases.UpsertTransactionUseCase
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.observe

private typealias Ctx = PipelineContext<ManageTransactionState, ManageTransactionIntent, Nothing>

private enum class Jobs {
    ObserveAccounts, ObserveCategories
}


class ManageTransactionContainer(
    private val transactionId: String?,
    private val getAccountsFlowUseCase: GetAccountsFlowUseCase,
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    private val upsertTransactionUseCase: UpsertTransactionUseCase,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val closeModal: () -> Unit
) : Container<ManageTransactionState, ManageTransactionIntent, Nothing> {
    val isCreateMode = transactionId == null

    @OptIn(DelicateStoreApi::class)
    override val store: Store<ManageTransactionState, ManageTransactionIntent, Nothing> =
        store(
            initial = getInitial()
        ) {
            fastConfig(
                name = "ManageTransaction", resetOnStop = false,
                doOnRecover = { state, e ->
                    ManageTransactionState.FatalError(e.message.orUnknown, (state as? ManageTransactionState.OK)?.form)
                }
            )
            install(
                manageTransactionBasePlugin(
                    getForm = { (this as? ManageTransactionState.OK)?.form },
                    setForm = { newForm ->
                        if (this is ManageTransactionState.OK) {
                            copy(form = newForm)
                        } else this
                    }
                )
            )

            init {
                if (!isCreateMode) {
                    setupInitial()
                }
            }

            val jobs = JobManager<Jobs>()

            whileSubscribed {
                observeAccounts(jobs)
                observeCategories(jobs)
            }


            customReduce { intent ->
                when (intent) {
                    ManageTransactionIntent.ClickedDelete -> if (!isCreateMode) {
                        withState<ManageTransactionState.OK, _> {
                            deleteTransactionUseCase(transactionId!!)
                            closeModal()
                        }
                    }

                    ManageTransactionIntent.ClickedSave -> {
                        withState<ManageTransactionState.OK, _> {
                            if (allValidated()) {
                                upsertTransactionUseCase(this.form.toDomain(transactionId))
                                closeModal()
                            }
                        }
                    }

                    ManageTransactionIntent.ClickedTryAgain -> updateState<ManageTransactionState.FatalError, _> {
                        getInitial(this.lastValidForm)
                    }
                }
            }
        }

    private fun Ctx.observeAccounts(jobs: JobManager<Jobs>) {
        observe(
            flow = getAccountsFlowUseCase(), key = Jobs.ObserveAccounts, jobs = jobs
        ) { accounts ->
            updateState<ManageTransactionState.OK, _> { copy(form = form.copy(accounts = accounts)) }
        }
    }

    private fun Ctx.observeCategories(jobs: JobManager<Jobs>) {
        observe(
            flow = getCategoriesFlowUseCase(), key = Jobs.ObserveCategories, jobs = jobs
        ) { categoriesLists ->
            updateState<ManageTransactionState.OK, _> {
                copy(
                    form = form.copy(
                        incomeCategories = categoriesLists.income,
                        outcomeCategories = categoriesLists.outcome,
                    )
                )
            }
        }
    }

    private suspend fun Ctx.setupInitial() {
        if (transactionId != null) {
            val transaction = getTransactionUseCase(transactionId)
            updateState<ManageTransactionState.OK, _> {
                getInitial(
                    form = transaction.toFormState(current = form)
                )
            }
        }
    }
}
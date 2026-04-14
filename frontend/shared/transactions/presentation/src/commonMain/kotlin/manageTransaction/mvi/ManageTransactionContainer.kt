package manageTransaction.mvi

import editors.usecases.account.GetAccountsFlowUseCase
import editors.usecases.category.GetCategoriesFlowUseCase
import manageTransaction.mvi.ManageTransactionType.*
import manageTransaction.mvi.base.manageTransactionBasePlugin
import manageTransaction.mvi.base.validated
import manageTransaction.mvi.base.validationHasErrors
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.withState
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.whileSubscribed
import transactions.usecases.UpsertTransactionUseCase
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.observe
import kotlin.uuid.ExperimentalUuidApi

private typealias Ctx = PipelineContext<ManageTransactionState, ManageTransactionIntent, Nothing>

private enum class Jobs {
    ObserveAccounts, ObserveCategories
}

fun ManageTransactionContainer.getInitial(
    form: ManageTransactionState.OK.FormState? = null
) =
    ManageTransactionState.OK(
        form = form ?: ManageTransactionState.OK.FormState(),
        isCreateMode = isCreateMode,
    ).getFullyValidated()

fun ManageTransactionState.OK.getFullyValidated(
) = copy(form = form.let {
    it.copy(validation = it.validate(), transactionType = it.transactionType.validated())
})

fun ManageTransactionState.OK.isFullyValidated(
) = !form.validation.hasErrors && !form.transactionType.validationHasErrors()


class ManageTransactionContainer(
    transactionId: String?,
    private val getAccountsFlowUseCase: GetAccountsFlowUseCase,
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    private val upsertTransactionUseCase: UpsertTransactionUseCase,
) : Container<ManageTransactionState, ManageTransactionIntent, Nothing> {
    val isCreateMode = transactionId == null

    @OptIn(DelicateStoreApi::class)
    override val store: Store<ManageTransactionState, ManageTransactionIntent, Nothing> =
        store(
            initial = getInitial()
        ) {
            fastConfig(
                name = "ManageTransaction", resetOnStop = false,
                doOnRecover = {
                    ManageTransactionState.FatalError(it.message.orUnknown, (this as? ManageTransactionState.OK)?.form)
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

            val jobs = JobManager<Jobs>()

            whileSubscribed {
                observeAccounts(jobs)
                observeCategories(jobs)
            }


            customReduce { intent ->
                when (intent) {
                    ManageTransactionIntent.ClickedDelete -> if (!isCreateMode) {
                        TODO()
                    }

                    ManageTransactionIntent.ClickedSave -> {
                        updateState<ManageTransactionState.OK, _> {
                            getFullyValidated()
                        }

                        withState<ManageTransactionState.OK, _> {
                            if (isFullyValidated()) {
                                if (isCreateMode) {
                                    createTransaction()
                                } else {
                                    // TODO: EDIT
                                }
                            }
                        }
                    }

                    ManageTransactionIntent.ClickedTryAgain -> updateState<ManageTransactionState.FatalError, _> {
                        getInitial(this.lastValidForm)
                    }
                }
            }
        }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun Ctx.createTransaction() {
        withState<ManageTransactionState.OK, _> {
            upsertTransactionUseCase(this.form.toDomain())
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
        ) { categories ->
            updateState<ManageTransactionState.OK, _> { copy(form = form.copy(categories = categories)) }
        }
    }
}
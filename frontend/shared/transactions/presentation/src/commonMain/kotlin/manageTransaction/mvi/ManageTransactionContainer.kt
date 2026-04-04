package manageTransaction.mvi

import editors.usecases.account.GetAccountsFlowUseCase
import editors.usecases.category.GetCategoriesFlowUseCase
import kotlinx.coroutines.launch
import manageTransaction.mvi.ManageTransactionType.*
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.JobManager
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import transactions.models.Transaction
import transactions.models.TransactionType
import transactions.usecases.UpsertTransactionUseCase
import utils.orUnknown
import utils.presentation.flowMVI.fastConfig
import utils.presentation.flowMVI.registerOrIgnore
import utils.toLocalDate
import utils.types.BigDecimal
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private typealias Ctx = PipelineContext<ManageTransactionState, ManageTransactionIntent, Nothing>

private enum class Jobs {
    ObserveAccounts, ObserveCategories
}

class ManageTransactionContainer(
    transactionId: String?,
    private val getAccountsFlowUseCase: GetAccountsFlowUseCase,
    private val getCategoriesFlowUseCase: GetCategoriesFlowUseCase,
    private val upsertTransactionUseCase: UpsertTransactionUseCase,
) : Container<ManageTransactionState, ManageTransactionIntent, Nothing> {
    private val isCreateMode = transactionId == null

    @OptIn(ExperimentalUuidApi::class, DelicateStoreApi::class)
    override val store: Store<ManageTransactionState, ManageTransactionIntent, Nothing> =
        store(
            initial = ManageTransactionState.OK(
                form = ManageTransactionState.OK.FormState(
                    categories = emptyList(),
                    accounts = emptyList(),
                    value = BigDecimal("0"),
                    transactionType = Outcome(categoryId = null, accountId = null),
                    note = "",
                    date = Clock.System.now().toLocalDate(),
                ),
                isCreateMode = isCreateMode,
            )
        ) {
            fastConfig(
                name = "ManageTransaction", resetOnStop = true,
                doOnRecover = { ManageTransactionState.FatalError(it.message.orUnknown) }
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


            reduce { intent ->
                println(intent)
                when (intent) {
                    ManageTransactionIntent.ClickedDelete -> if (!isCreateMode) {
                        TODO()
                    }

                    ManageTransactionIntent.ClickedSave -> if (true) {
                        withState {
                            if (this is ManageTransactionState.OK) {
                                val (accountId, categoryId) =
                                    when (val type = this.form.transactionType) {
                                        is Income -> type.accountId to type.categoryId
                                        is Outcome -> type.accountId to type.categoryId
                                        is Transfer -> type.fromId to null
                                    }

                                val account = this.form.accounts.firstOrNull {
                                    it.id == accountId
                                }
                                val category = this.form.categories.firstOrNull {
                                    it.id == categoryId
                                }
                                upsertTransactionUseCase(
                                    Transaction(
                                        id = Uuid.generateV7().toString(),
                                        value = this.form.value,
                                        type = when (val t = this.form.transactionType) {
                                            is Income -> TransactionType.Income(
                                                category = category!!
                                            )

                                            is Outcome -> TransactionType.Outcome(
                                                category = category!!
                                            )

                                            is Transfer -> TransactionType.Transfer(
                                                from = account!!,
                                                to = this.form.accounts.first {
                                                    it.id == t.toId
                                                }
                                            )
                                        },
                                        account = account!!,
                                        note = this.form.note,
                                        date = this.form.date
                                    )
                                )
                                updateState {
                                    copy(
                                        form = form.copy(
                                            value = BigDecimal("0"),
                                            transactionType = Outcome(
                                                categoryId = null,
                                                accountId = null
                                            ),
                                            note = "",
                                            date = Clock.System.now().toLocalDate(),
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        TODO()
                    }
                }
            }
        }

    private fun Ctx.observeAccounts(jobs: JobManager<Jobs>) {
        launch {
            getAccountsFlowUseCase().collect { accounts ->
                updateState<ManageTransactionState.OK, _> {
                    println("meowx")
                    this.copy(form = this.form.copy(accounts = accounts))
                }
            }
        }.registerOrIgnore(jobs, Jobs.ObserveAccounts)
    }

    private fun Ctx.observeCategories(jobs: JobManager<Jobs>) {
        launch {
            getCategoriesFlowUseCase().collect { categories ->
                updateState<ManageTransactionState.OK, _> {
                    if (form.categories === categories) return@updateState this

                    copy(form = form.copy(categories = categories))
                }
            }
        }.registerOrIgnore(jobs, Jobs.ObserveCategories)
    }
}
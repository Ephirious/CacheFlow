package stats

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.instancekeeper.getOrCreateSimple
import editors.models.Account
import editors.usecases.account.GetAccountsFlowUseCase
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import transactions.models.Transaction
import transactions.usecases.GetTransactionsFlowUseCase
import utils.interop.JsValue
import utils.interop.asJsValue
import utils.presentation.launchPersistentCoroutine
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface StatsComponent : ComponentContext {
    @JsName("state")
    val jsState: JsValue<StatsState>

    @Suppress("unused")
    fun intent(intent: StatsIntent)
}

class RealStatsComponent(
    componentCtx: ComponentContext,
) : StatsComponent, KoinComponent, ComponentContext by componentCtx {
    private val getAccountsFlowUseCase: GetAccountsFlowUseCase = get()
    private val getTransactionsFlowUseCase: GetTransactionsFlowUseCase = get()

    private val sourceData = instanceKeeper.getOrCreateSimple(key = "StatsSourceData") {
        StatsSourceData()
    }
    private val _state = instanceKeeper.getOrCreateSimple(key = "StatsState") {
        MutableValue(StatsCalculator.initialState())
    }

    override val jsState: JsValue<StatsState> by lazy { _state.asJsValue() }

    init {
        launchPersistentCoroutine(key = "StatsSubscription") {
            combine(getAccountsFlowUseCase(), getTransactionsFlowUseCase(accountId = null)) { accounts, transactions ->
                StatsSourceData(accounts, transactions)
            }.collect { data ->
                sourceData.accounts = data.accounts
                sourceData.transactions = data.transactions

                val current = _state.value
                val selectedAccountId = current.selectedAccountId.takeIf { selectedId ->
                    data.accounts.any { it.id == selectedId }
                }
                _state.value = StatsCalculator.buildState(
                    selectedAccountId = selectedAccountId,
                    period = current.period,
                    metric = current.metric,
                    accounts = data.accounts,
                    transactions = data.transactions
                )
            }
        }
    }

    override fun intent(intent: StatsIntent) {
        val current = _state.value
        val nextState = when (intent) {
            is StatsIntent.SelectAccount -> StatsCalculator.buildState(
                selectedAccountId = intent.accountId,
                period = current.period,
                metric = current.metric,
                accounts = sourceData.accounts,
                transactions = sourceData.transactions
            )

            is StatsIntent.SelectMetric -> StatsCalculator.buildState(
                selectedAccountId = current.selectedAccountId,
                period = current.period,
                metric = intent.metric,
                accounts = sourceData.accounts,
                transactions = sourceData.transactions
            )

            is StatsIntent.SelectPresetPeriod -> StatsCalculator.buildState(
                selectedAccountId = current.selectedAccountId,
                period = current.period.copy(preset = intent.preset),
                metric = current.metric,
                accounts = sourceData.accounts,
                transactions = sourceData.transactions
            )

            is StatsIntent.SelectCustomPeriod -> {
                val custom = StatsCalculator.parseCustomPeriod(
                    from = intent.fromIsoDate,
                    to = intent.toIsoDate,
                    fallback = current.period.custom
                )
                StatsCalculator.buildState(
                    selectedAccountId = current.selectedAccountId,
                    period = current.period.copy(preset = null, custom = custom),
                    metric = current.metric,
                    accounts = sourceData.accounts,
                    transactions = sourceData.transactions
                )
            }
        }
        _state.value = nextState
    }
}

private class StatsSourceData(
    var accounts: List<Account> = emptyList(),
    var transactions: List<Transaction> = emptyList(),
)

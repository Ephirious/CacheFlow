package main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import kotlinx.coroutines.launch
import main.mvi.MainContainer
import main.mvi.MainIntent
import main.mvi.MainState
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import summary.RealSummaryComponent
import summary.SummaryComponent
import summary.mvi.SummaryContainer
import transactions.RealTransactionsComponent
import transactions.TransactionsComponent
import transactions.mvi.TransactionsContainer
import utils.interop.JsValue
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface MainComponent : ComponentContext {

    val transactionsComponent: TransactionsComponent
    val summaryComponent: SummaryComponent

    @JsName("state")
    val jsState: JsValue<MainState>

    @Suppress("unused")
    fun intent(intent: MainIntent)

    fun restartAllComponents()
}


class RealMainComponent(
    componentCtx: ComponentContext,
    container: () -> MainContainer,
) : MainComponent, KoinComponent, ComponentContext by componentCtx,
    Store<MainState, MainIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override val jsState: JsValue<MainState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }


    private fun throwErrorFromChild(message: () -> String) {
        intent(MainIntent.ThrowError(message()))
    }

    override val transactionsComponent: TransactionsComponent =
        RealTransactionsComponent(
            componentCtx.childContext("Transactions"),
            container = {
                TransactionsContainer(
                    throwErrorToParent = ::throwErrorFromChild,
                    getTransactionsFlowUseCase = get()
                )
            }
        )
    override val summaryComponent: SummaryComponent =
        RealSummaryComponent(
            componentCtx.childContext("Summary"),
            container = {
                SummaryContainer(
                    throwErrorToParent = ::throwErrorFromChild,
                    getAccountsFlowUseCase = get()
                )
            }
        )


    // стрельнет?
    override fun restartAllComponents() {
        componentCoroutineScope.launch {
            (transactionsComponent as RealTransactionsComponent).close()
            transactionsComponent.start(transactionsComponent.componentCoroutineScope)

            (summaryComponent as RealSummaryComponent).close()
            summaryComponent.start(summaryComponent.componentCoroutineScope)
        }
    }


}
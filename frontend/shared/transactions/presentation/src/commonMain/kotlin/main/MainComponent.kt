package main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import main.mvi.MainAction
import main.mvi.MainContainer
import main.mvi.MainIntent
import main.mvi.MainState
import manageTransaction.ManageTransactionComponent
import manageTransaction.RealManageTransactionComponent
import manageTransaction.mvi.ManageTransactionContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.essenty.dsl.subscribe
import summary.RealSummaryComponent
import summary.SummaryComponent
import summary.mvi.SummaryContainer
import transactions.RealTransactionsComponent
import transactions.TransactionsComponent
import transactions.mvi.TransactionsContainer
import utils.interop.JsChildSlot
import utils.interop.JsValue
import utils.interop.asJsSlot
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface MainComponent : ComponentContext {


    val jsManageTransactionSlot: JsValue<JsChildSlot<ManageTransactionComponent>>

    val transactionsComponent: TransactionsComponent
    val summaryComponent: SummaryComponent

    @JsName("state")
    val jsState: JsValue<MainState>

    @Suppress("unused")
    fun intent(intent: MainIntent)

    fun subscribeActions(onAction: (MainAction) -> Unit)

    fun restartAllComponents()

    fun setIsManageTransactionOpen(isOpen: Boolean)
    fun openTransactionToEdit(transactionId: String)
}


@Serializable
private data class ManageTransactionConfig(
    val transactionId: String?,
)

class RealMainComponent(
    componentCtx: ComponentContext,
    container: () -> MainContainer,
) : MainComponent, KoinComponent, ComponentContext by componentCtx,
    Store<MainState, MainIntent, MainAction> by componentCtx.retainedStore(factory = container) {


    private val manageTransactionNavigation = SlotNavigation<ManageTransactionConfig>()


    override fun setIsManageTransactionOpen(isOpen: Boolean) {
        if (isOpen) {
            manageTransactionNavigation.activate(ManageTransactionConfig(null))
        } else {
            manageTransactionNavigation.dismiss()
        }
    }

    override fun openTransactionToEdit(transactionId: String) {
        manageTransactionNavigation.activate(ManageTransactionConfig(transactionId))
    }

    private val manageTransactionSlot: Value<ChildSlot<*, ManageTransactionComponent>> =
        childSlot(
            source = manageTransactionNavigation,
            serializer = ManageTransactionConfig.serializer(),
            handleBackButton = true,
        ) { config, childCtx ->
            RealManageTransactionComponent(
                componentCtx = childCtx,
                container = {
                    ManageTransactionContainer(
                        transactionId = config.transactionId,
                        getAccountsFlowUseCase = get(),
                        getCategoriesFlowUseCase = get(),
                        upsertTransactionUseCase = get(),
                        closeRequest = { intent(MainIntent.CloseManage) }
                    )
                }
            )
        }

    override val jsManageTransactionSlot: JsValue<JsChildSlot<ManageTransactionComponent>> by lazy {
        manageTransactionSlot.asJsSlot()
    }

    override val jsState: JsValue<MainState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }

    override fun subscribeActions(onAction: (MainAction) -> Unit) {
        subscribe(scope = componentCoroutineScope) {
            actions.collect { action ->
                onAction(action)
            }
        }
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
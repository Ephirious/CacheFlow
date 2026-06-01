package transactions

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import filters.FiltersComponent
import filters.RealFiltersComponent
import filters.mvi.FiltersContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.essenty.dsl.subscribe
import transactions.models.TransactionFilters
import transactions.mvi.TransactionsAction
import transactions.mvi.TransactionsContainer
import transactions.mvi.TransactionsIntent
import transactions.mvi.TransactionsState
import utils.interop.JsChildSlot
import utils.interop.JsValue
import utils.interop.asJsSlot
import utils.interop.jsStateSubscribe
import utils.popUrlSegment
import utils.presentation.componentCoroutineScope
import utils.pushUrlSegment
import kotlin.getValue
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface TransactionsComponent : ComponentContext {


    val jsFiltersSlot: JsValue<JsChildSlot<FiltersComponent>>

    fun subscribeActions(onAction: (TransactionsAction) -> Unit): () -> Unit

    fun setIsFiltersOpen(isOpen: Boolean)

    @JsName("state")
    val jsState: JsValue<TransactionsState>

    @Suppress("unused")
    fun intent(intent: TransactionsIntent)
}


class RealTransactionsComponent(
    componentCtx: ComponentContext,
    container: () -> TransactionsContainer,
) : TransactionsComponent, KoinComponent, ComponentContext by componentCtx,
    Store<TransactionsState, TransactionsIntent, TransactionsAction> by componentCtx.retainedStore(factory = container) {

    private val filtersSlotNavigation = SlotNavigation<Unit>()

    override fun setIsFiltersOpen(isOpen: Boolean) {
        if (isOpen) {
            filtersSlotNavigation.activate(Unit)
            pushUrlSegment("filters")
        } else {
            filtersSlotNavigation.dismiss()
            popUrlSegment("filters")
        }
    }

    private val filtersSlot: Value<ChildSlot<Unit, FiltersComponent>> =
        childSlot(
            source = filtersSlotNavigation,
            serializer = null, // т.к. после перезагрузки багуется в вебе //ManageTransactionConfig.serializer(),
            handleBackButton = false,
        ) { _, childCtx ->
            RealFiltersComponent(
                componentCtx = childCtx,
                container = {
                    FiltersContainer(
                        initialFilters = TransactionFilters(),
                        getAccountsFlowUseCase = get(),
                        getCategoriesFlowUseCase = get(),
                        onApply = {
                            intent(TransactionsIntent.FiltersApplied(it))
                        },
                        onClose = { intent(TransactionsIntent.CloseFilters) }
                    )
                }
            )
        }

    override val jsFiltersSlot: JsValue<JsChildSlot<FiltersComponent>> by lazy {
        filtersSlot.asJsSlot()
    }

    override fun subscribeActions(onAction: (TransactionsAction) -> Unit): () -> Unit {
        val job = subscribe(scope = componentCoroutineScope) {
            actions.collect { action ->
                onAction(action)
            }
        }
        return { job.cancel() }
    }

    override val jsState: JsValue<TransactionsState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }
}
package manageTransaction

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import manageTransaction.modals.ManageTransactionModalChild
import manageTransaction.modals.ManageTransactionModalConfig
import manageTransaction.modals.modalChild
import manageTransaction.mvi.ManageTransactionContainer
import manageTransaction.mvi.ManageTransactionIntent
import manageTransaction.mvi.ManageTransactionState
import org.koin.core.component.KoinComponent
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import utils.interop.JsChildSlot
import utils.interop.JsValue
import utils.interop.asJsSlot
import utils.interop.jsStateSubscribe
import utils.presentation.componentCoroutineScope
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface ManageTransactionComponent : ComponentContext {

    val jsModalSlot: JsValue<JsChildSlot<ManageTransactionModalChild>>

    @JsName("state")
    val jsState: JsValue<ManageTransactionState>

    @Suppress("unused")
    fun intent(intent: ManageTransactionIntent)

    fun dismissSlot()


    fun createAccountClick()
    fun createCategoryClick()

}


class RealManageTransactionComponent(
    componentCtx: ComponentContext,
    container: () -> ManageTransactionContainer,
) : ManageTransactionComponent, KoinComponent, ComponentContext by componentCtx,
    Store<ManageTransactionState, ManageTransactionIntent, Nothing> by componentCtx.retainedStore(factory = container) {

    override val jsState: JsValue<ManageTransactionState> by lazy {
        jsStateSubscribe(scope = componentCoroutineScope, lifecycleOwner = this)
    }


    val modalNavigation = SlotNavigation<ManageTransactionModalConfig>()


    private val modalSlot: Value<ChildSlot<ManageTransactionModalConfig, ManageTransactionModalChild>> = modalChild()

    override val jsModalSlot: JsValue<JsChildSlot<ManageTransactionModalChild>> by lazy {
        modalSlot.asJsSlot()
    }

    override fun dismissSlot() {
        modalNavigation.dismiss()
    }

    override fun createAccountClick() {
        modalNavigation.activate(ManageTransactionModalConfig.CreateAccount)
    }

    override fun createCategoryClick() {
        modalNavigation.activate(ManageTransactionModalConfig.CreateCategory)
    }
}
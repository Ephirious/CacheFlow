package settings.pages.accounts

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.instancekeeper.getOrCreateSimple
import editors.models.Account
import editors.usecases.account.GetAccountsFlowUseCase
import settings.pages.SettingsPageComponent
import utils.interop.JsValue
import utils.interop.asJsValue
import utils.presentation.launchPersistentCoroutine
import kotlin.js.JsExport
import kotlin.js.JsName


@JsExport
interface AccountsComponent : SettingsPageComponent {
    @JsName("accountsList")
    val jsAccounts: JsValue<List<Account>>
}

class RealAccountsComponent(
    componentCtx: ComponentContext,
    getAccountsFlowUseCase: GetAccountsFlowUseCase
) : AccountsComponent, ComponentContext by componentCtx {


    private val _accounts = instanceKeeper.getOrCreateSimple(key = "Categories") {
        MutableValue(emptyList<Account>())
    }

    override val jsAccounts: JsValue<List<Account>> by lazy { _accounts.asJsValue() }

    init {
        launchPersistentCoroutine(key = "CategoriesSubscription") {
            getAccountsFlowUseCase().collect { accounts ->
                _accounts.value = accounts
            }
        }
    }

    override fun onCreateClick() {
        TODO("Not yet implemented")
    }

}
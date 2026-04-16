package settings.pages.accounts

import com.arkivanov.decompose.ComponentContext
import settings.pages.SettingsPageComponent
import kotlin.js.JsExport


@JsExport
interface AccountsComponent : SettingsPageComponent {

}

class RealAccountsComponent(
    componentCtx: ComponentContext
) : AccountsComponent, ComponentContext by componentCtx {
    override fun onCreateClick() {
        TODO("Not yet implemented")
    }
}
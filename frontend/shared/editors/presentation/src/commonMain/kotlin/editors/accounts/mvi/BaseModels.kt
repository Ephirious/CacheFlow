package editors.accounts.mvi

import core_validation.GenerateValidator
import core_validation.data.account.AccountTitleValidator
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import utils.annotations.DataCopyable
import utils.types.HexColor
import kotlin.js.JsExport

@DataCopyable
interface ManageAccountBaseState<F : ManageAccountFormBaseState<*>> : MVIState {


    interface OK<F : ManageAccountFormBaseState<*>> : ManageAccountBaseState<F> {
        val form: F
    }

    interface FatalError<F : ManageAccountFormBaseState<*>> : ManageAccountBaseState<F> {
        val message: String
        val lastForm: F?
    }
}

@JsExport
@DataCopyable
@GenerateValidator
interface ManageAccountFormBaseState<V> : MVIState {
    @AccountTitleValidator
    val title: String

    val color: HexColor
    val validation: V
}

@JsExport
sealed class ManageAccountBaseIntent : MVIIntent {

    sealed interface Internal

    data class ChangedTitle(val title: String) : ManageAccountBaseIntent(), Internal
    data class ChangedColor(val color: String) : ManageAccountBaseIntent(), Internal
    data object ClickedTryAgain : ManageAccountBaseIntent(), Internal
}
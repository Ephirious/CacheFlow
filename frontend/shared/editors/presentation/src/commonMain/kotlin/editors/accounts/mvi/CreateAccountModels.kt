package editors.accounts.mvi

import utils.annotations.DataCopyableNode
import utils.annotations.GenerateValidator
import utils.annotations.validation.StringAmount
import utils.types.HexColor
import kotlin.js.JsExport

@JsExport
@DataCopyableNode
sealed class CreateAccountState : ManageAccountBaseState<CreateAccountFormState> {

    @DataCopyableNode
    data class OK(
        override val form: CreateAccountFormState
    ) : CreateAccountState(), ManageAccountBaseState.OK<CreateAccountFormState> {
        fun getForm() = form
    }

    @DataCopyableNode
    data class FatalError(
        override val message: String,
        override val lastForm: CreateAccountFormState?
    ) : CreateAccountState(), ManageAccountBaseState.FatalError<CreateAccountFormState> {
        fun getMessage() = message
        fun getLastForm() = lastForm
    }
}

@JsExport
@GenerateValidator
@DataCopyableNode
data class CreateAccountFormState(
    @StringAmount(param = false)
    val initialBalance: String,
    override val validation: CreateAccountFormValidationErrors,
    override val title: String,
    override val color: HexColor,
) : ManageAccountFormBaseState<CreateAccountFormValidationErrors>


@JsExport
sealed class CreateAccountIntent : ManageAccountBaseIntent() {
    data object ClickedCreate : CreateAccountIntent()
    data class ChangedBalance(val balance: String) : CreateAccountIntent()
}
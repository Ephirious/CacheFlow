package editors.accounts.mvi

import utils.annotations.DataCopyableNode
import utils.types.HexColor
import kotlin.js.JsExport

@JsExport
@DataCopyableNode
sealed class EditAccountState : ManageAccountBaseState<EditAccountFormState> {

    @DataCopyableNode
    data class OK(
        override val form: EditAccountFormState
    ) : EditAccountState(), ManageAccountBaseState.OK<EditAccountFormState> {
        fun getForm() = form
    }

    @DataCopyableNode
    data class FatalError(
        override val message: String,
        override val lastForm: EditAccountFormState?
    ) : EditAccountState(), ManageAccountBaseState.FatalError<EditAccountFormState> {
        fun getMessage() = message
        fun getLastForm() = lastForm
    }
}

@JsExport
@DataCopyableNode
data class EditAccountFormState(
    override val validation: ManageAccountFormBaseValidationErrors,
    override val title: String,
    override val color: HexColor,
) : ManageAccountFormBaseState<ManageAccountFormBaseValidationErrors>


@JsExport
sealed class EditAccountIntent : ManageAccountBaseIntent() {
    data object ClickedEdit : EditAccountIntent()
}
package editors.categories.mvi


import utils.annotations.DataCopyableNode
import kotlin.js.JsExport

@JsExport
@DataCopyableNode
sealed class EditCategoryState : ManageCategoryBaseState<EditFormState> {

    @DataCopyableNode
    data class OK(
        override val form: EditFormState
    ) : EditCategoryState(), ManageCategoryBaseState.OK<EditFormState> {
        fun getForm() = form
    }

    @DataCopyableNode
    data class FatalError(
        override val message: String,
        override val lastForm: EditFormState?
    ) : EditCategoryState(), ManageCategoryBaseState.FatalError<EditFormState> {
        fun getMessage() = message
        fun getLastForm() = lastForm
    }
}

@JsExport
@DataCopyableNode
data class EditFormState(
    override val name: String,
    override val emoji: String,
    override val validation: ManageCategoryFormBaseValidationErrors,
) : ManageCategoryFormBaseState<ManageCategoryFormBaseValidationErrors>


@JsExport
sealed class EditCategoryIntent : ManageCategoryBaseIntent() {
    data object ClickedEdit : EditCategoryIntent()
}
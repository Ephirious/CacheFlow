package editors.categories.mvi


import utils.annotations.DataCopyableNode
import kotlin.js.JsExport

@JsExport
@DataCopyableNode
sealed class EditCategoryState : ManageCategoryBaseState<EditCategoryFormState> {

    @DataCopyableNode
    @Suppress("unused")
    data class OK(
        override val form: EditCategoryFormState
    ) : EditCategoryState(), ManageCategoryBaseState.OK<EditCategoryFormState> {
        fun getForm() = form
    }

    @DataCopyableNode
    @Suppress("unused")
    data class FatalError(
        override val message: String,
        override val lastForm: EditCategoryFormState?
    ) : EditCategoryState(), ManageCategoryBaseState.FatalError<EditCategoryFormState> {
        fun getMessage() = message
        fun getLastForm() = lastForm
    }
}

@JsExport
@DataCopyableNode
data class EditCategoryFormState(
    override val title: String,
    override val emoji: String,
    override val validation: ManageCategoryFormBaseValidationErrors,
) : ManageCategoryFormBaseState<ManageCategoryFormBaseValidationErrors>


@JsExport
sealed class EditCategoryIntent : ManageCategoryBaseIntent() {
    data object ClickedEdit : EditCategoryIntent()
    data object ClickedDelete : EditCategoryIntent()
}
package editors.categories.mvi

import dbEnums.CategoryType
import utils.annotations.DataCopyableNode
import kotlin.js.JsExport

@JsExport
@DataCopyableNode
sealed class CreateCategoryState : ManageCategoryBaseState<CreateCategoryFormState> {

    @DataCopyableNode
    @Suppress("unused")
    data class OK(
        override val form: CreateCategoryFormState
    ) : CreateCategoryState(), ManageCategoryBaseState.OK<CreateCategoryFormState> {
        fun getForm() = form
    }

    @DataCopyableNode
    @Suppress("unused")
    data class FatalError(
        override val message: String,
        override val lastForm: CreateCategoryFormState?
    ) : CreateCategoryState(), ManageCategoryBaseState.FatalError<CreateCategoryFormState> {
        fun getMessage() = message
        fun getLastForm() = lastForm
    }
}

@JsExport
@DataCopyableNode
data class CreateCategoryFormState(
    val categoryType: CategoryType,
    override val title: String,
    override val emoji: String,
    override val validation: ManageCategoryFormBaseValidationErrors,
) : ManageCategoryFormBaseState<ManageCategoryFormBaseValidationErrors>


@JsExport
sealed class CreateCategoryIntent : ManageCategoryBaseIntent() {
    data object ClickedCreate : CreateCategoryIntent()
    data class ChangedCategoryType(val type: String) : CreateCategoryIntent()
}
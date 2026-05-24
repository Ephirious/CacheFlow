package editors.categories.mvi

import dbEnums.CategoryType


fun CreateCategoryContainer.getInitial(
    form: CreateCategoryFormState? = null
) =
    CreateCategoryState.OK(
        form = form ?: CreateCategoryFormState(
            categoryType = CategoryType.OUTCOME,
            title = "",
            emoji = "",
            validation = ManageCategoryFormBaseValidationErrors()
        ),
    ).allValidate()

fun CreateCategoryState.OK.allValidate(
): CreateCategoryState.OK = copy(form = form.let {
    it.copy(validation = it.validate())
})

fun CreateCategoryState.OK.allValidated(
) = allValidate().isValid()

fun CreateCategoryState.OK.isValid(
) = !form.validation.hasErrors
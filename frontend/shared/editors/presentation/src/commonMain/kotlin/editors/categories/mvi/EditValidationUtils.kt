package editors.categories.mvi


fun EditCategoryContainer.getInitial(
    form: EditCategoryFormState? = null
) =
    EditCategoryState.OK(
        form = form ?: EditCategoryFormState(
            title = "",
            emoji = "",
            validation = ManageCategoryFormBaseValidationErrors()
        )
    ).allValidate()

fun EditCategoryState.OK.allValidate(
) = copy(form = form.let {
    it.copy(validation = it.validate())
})

fun EditCategoryState.OK.allValidated(
) = allValidate().isValid()

fun EditCategoryState.OK.isValid(
) = !form.validation.hasErrors
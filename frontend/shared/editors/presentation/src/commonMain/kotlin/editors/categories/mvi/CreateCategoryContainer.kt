package editors.categories.mvi

import dbEnums.CategoryType
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig


class CreateCategoryContainer(
) : Container<CreateCategoryState, CreateCategoryIntent, Nothing> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<CreateCategoryState, CreateCategoryIntent, Nothing> =
        store(
            initial = CreateCategoryState.OK(
                form = CreateFormState(
                    categoryType = CategoryType.OUTCOME,
                    name = "",
                    emoji = "",
                    validation = ManageCategoryFormBaseValidationErrors()
                )
            )

        ) {
            fastConfig(
                name = "CreateCategory", resetOnStop = false,
                doOnRecover = {
                    CreateCategoryState.FatalError(
                        it.message.orUnknown,
                        (this as? CreateCategoryState.OK)?.form
                    )
                }
            )
            install(
                manageCategoryBasePlugin(
                    getState = { this },
                    setState = { newState -> newState as CreateCategoryState },
                    makeOK = { form -> CreateCategoryState.OK(form) }
                )
            )


            customReduce { intent ->
                when (intent) {
                    CreateCategoryIntent.ClickedSave -> TODO()
                    is CreateCategoryIntent.ChangedCategoryType -> updateState<CreateCategoryState.OK, _> {
                        copy(form = form.copy(categoryType = if (intent.type == "income") CategoryType.INCOME else CategoryType.OUTCOME))
                    }
                }
            }
        }
}
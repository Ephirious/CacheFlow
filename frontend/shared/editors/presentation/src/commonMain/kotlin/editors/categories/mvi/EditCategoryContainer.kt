package editors.categories.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig


class EditCategoryContainer(
    val id: String
) : Container<EditCategoryState, EditCategoryIntent, Nothing> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<EditCategoryState, EditCategoryIntent, Nothing> =
        store(
            initial = EditCategoryState.OK(
                form = EditFormState(
                    name = "",
                    emoji = "",
                    validation = ManageCategoryFormBaseValidationErrors()
                )
            )

        ) {
            fastConfig(
                name = "CreateCategory", resetOnStop = false,
                doOnRecover = {
                    EditCategoryState.FatalError(
                        it.message.orUnknown,
                        (this as? EditCategoryState.OK)?.form
                    )
                }
            )
            install(
                manageCategoryBasePlugin(
                    getState = { this },
                    setState = { newState -> newState as EditCategoryState },
                    makeOK = { form -> EditCategoryState.OK(form) }
                )
            )


            customReduce { intent ->
                when (intent) {
                    EditCategoryIntent.ClickedEdit -> TODO()
                }
            }
        }
}
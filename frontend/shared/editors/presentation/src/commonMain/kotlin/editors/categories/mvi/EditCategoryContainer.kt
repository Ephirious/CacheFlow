package editors.categories.mvi

import editors.usecases.category.DeleteCategoryUseCase
import editors.usecases.category.EditCategoryUseCase
import editors.usecases.category.GetCategoryByIdUseCase
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.withState
import pro.respawn.flowmvi.plugins.init
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig


private typealias Ctx = PipelineContext<EditCategoryState, EditCategoryIntent, Nothing>

class EditCategoryContainer(
    val id: String,
    val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    val editCategoryUseCase: EditCategoryUseCase,
    val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val closeModal: () -> Unit
) : Container<EditCategoryState, EditCategoryIntent, Nothing> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<EditCategoryState, EditCategoryIntent, Nothing> =
        store(
            initial = getInitial()
        ) {
            fastConfig(
                name = "CreateCategory", resetOnStop = false,
                doOnRecover = { state, e ->
                    EditCategoryState.FatalError(
                        e.message.orUnknown,
                        (state as? EditCategoryState.OK)?.form
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

            init {
                setupInitial()
            }


            customReduce { intent ->
                when (intent) {
                    EditCategoryIntent.ClickedEdit -> editCategory()
                    EditCategoryIntent.ClickedDelete -> deleteCategory()
                }
            }
        }

    private suspend fun Ctx.setupInitial() {
        val category = getCategoryByIdUseCase(id)
        updateState<EditCategoryState.OK, _> {
            EditCategoryState.OK(
                form = EditCategoryFormState(
                    title = category.name,
                    emoji = category.emoji,
                    validation = ManageCategoryFormBaseValidationErrors()
                )
            )
        }
    }

    private suspend fun Ctx.editCategory() {
        withState<EditCategoryState.OK, _> {
            if (allValidated()) {
                editCategoryUseCase(
                    id = id,
                    name = this.form.title,
                    emoji = this.form.emoji,
                )
                closeModal()
            }
        }
    }

    private suspend fun Ctx.deleteCategory() {
        withState<EditCategoryState.OK, _> {
            deleteCategoryUseCase(id = id)
            closeModal()
        }
    }
}
package editors.categories.mvi

import dbEnums.CategoryType
import editors.usecases.category.CreateCategoryUseCase
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.withState
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig


private typealias CtxCreate = PipelineContext<CreateCategoryState, CreateCategoryIntent, Nothing>

class CreateCategoryContainer(
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val closeModal: () -> Unit
) : Container<CreateCategoryState, CreateCategoryIntent, Nothing> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<CreateCategoryState, CreateCategoryIntent, Nothing> =
        store(
            initial = CreateCategoryState.OK(
                form = CreateFormState(
                    categoryType = CategoryType.OUTCOME,
                    title = "",
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
                    CreateCategoryIntent.ClickedCreate -> createCategory()
                    is CreateCategoryIntent.ChangedCategoryType -> updateState<CreateCategoryState.OK, _> {
                        copy(form = form.copy(categoryType = if (intent.type == "income") CategoryType.INCOME else CategoryType.OUTCOME))
                    }
                }
            }
        }

    private suspend fun CtxCreate.createCategory() {
        withState<CreateCategoryState.OK, _> {
            createCategoryUseCase(
                name = this.form.title,
                emoji = this.form.emoji,
                type = this.form.categoryType
            )
            closeModal()
        }

    }
}
package editors.accounts.mvi

import editors.usecases.account.GetAccountByIdUseCase
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.init
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import utils.types.HexColor

private typealias Ctx = PipelineContext<EditAccountState, EditAccountIntent, Nothing>

class EditAccountContainer(
    val id: String,
    val getAccountByIdUseCase: GetAccountByIdUseCase
) : Container<EditAccountState, EditAccountIntent, Nothing> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<EditAccountState, EditAccountIntent, Nothing> =
        store(
            initial = EditAccountState.OK(
                form = EditAccountFormState(
                    title = "",
                    color = HexColor("#FF0000"),
                    validation = ManageAccountFormBaseValidationErrors()
                )
            ).let {
                it.copy(form = it.form.validated() as EditAccountFormState)
            }

        ) {
            fastConfig(
                name = "CreateCategory", resetOnStop = false,
                doOnRecover = {
                    EditAccountState.FatalError(
                        it.message.orUnknown,
                        (this as? EditAccountState.OK)?.form
                    )
                }
            )
            install(
                manageAccountBasePlugin(
                    getState = { this },
                    setState = { newState -> newState as EditAccountState },
                    makeOK = { form -> EditAccountState.OK(form) }
                )
            )

            init {
                setupInitial()
            }

            customReduce { intent ->
                when (intent) {
                    EditAccountIntent.ClickedEdit -> TODO()
                }
            }
        }

    private suspend fun Ctx.setupInitial() {
        val account = getAccountByIdUseCase(id)
        updateState<EditAccountState.OK, _> {
            EditAccountState.OK(
                form = EditAccountFormState(
                    title = account.title,
                    color = account.color,
                    validation = ManageAccountFormBaseValidationErrors()
                )
            )
        }
    }
}
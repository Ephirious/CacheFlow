package editors.accounts.mvi

import editors.usecases.account.DeleteAccountUseCase
import editors.usecases.account.EditAccountUseCase
import editors.usecases.account.GetAccountByIdUseCase
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

private typealias CtxEdit = PipelineContext<EditAccountState, EditAccountIntent, Nothing>

class EditAccountContainer(
    val id: String,
    val getAccountByIdUseCase: GetAccountByIdUseCase,
    val editAccountUseCase: EditAccountUseCase,
    val deleteAccountUseCase: DeleteAccountUseCase,
    private val closeModal: () -> Unit
) : Container<EditAccountState, EditAccountIntent, Nothing> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<EditAccountState, EditAccountIntent, Nothing> =
        store(
            initial = getInitial()
        ) {
            fastConfig(
                name = "EditCategory", resetOnStop = false,
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
                    EditAccountIntent.ClickedEdit -> editAccount()
                    EditAccountIntent.ClickedDelete -> deleteAccount()
                }
            }
        }

    private suspend fun CtxEdit.setupInitial() {
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

    private suspend fun CtxEdit.editAccount() {
        withState<EditAccountState.OK, _> {
            if (allValidated()) {
                editAccountUseCase(
                    id = id,
                    name = this.form.title,
                    color = this.form.color
                )
                closeModal()
            }
        }
    }

    private suspend fun CtxEdit.deleteAccount() {
        withState<EditAccountState.OK, _> {
            deleteAccountUseCase(id = id)
            closeModal()
        }
    }
}
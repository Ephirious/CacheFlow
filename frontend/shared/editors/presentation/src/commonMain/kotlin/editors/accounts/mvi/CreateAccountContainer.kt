package editors.accounts.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import utils.orUnknown
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig
import utils.types.HexColor


class CreateAccountContainer(
) : Container<CreateAccountState, CreateAccountIntent, Nothing> {

    @OptIn(DelicateStoreApi::class)
    override val store: Store<CreateAccountState, CreateAccountIntent, Nothing> =
        store(
            initial = CreateAccountState.OK(
                form = CreateAccountFormState(
                    initialBalance = "",
                    title = "",
                    color = HexColor("#FF0000"),
                    validation = CreateAccountFormValidationErrors()
                ).validated()
            )

        ) {
            fastConfig(
                name = "CreateCategory", resetOnStop = false,
                doOnRecover = {
                    CreateAccountState.FatalError(
                        it.message.orUnknown,
                        (this as? CreateAccountState.OK)?.form
                    )
                }
            )
            install(
                manageAccountBasePlugin(
                    getState = { this },
                    setState = { newState -> newState as CreateAccountState },
                    makeOK = { form -> CreateAccountState.OK(form) }
                )
            )


            customReduce { intent ->
                when (intent) {
                    is CreateAccountIntent.ChangedBalance -> updateState<CreateAccountState.OK, _> {
                        copy(
                            form = form.copy(initialBalance = intent.balance).validated(
                                CreateAccountFormValidationFields.initialBalance
                            )
                        )
                    }

                    CreateAccountIntent.ClickedCreate -> TODO()
                }
            }
        }
}
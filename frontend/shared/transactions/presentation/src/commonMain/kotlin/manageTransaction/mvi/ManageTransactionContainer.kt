package manageTransaction.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.util.withType
import utils.BigDecimal
import utils.orUnknown
import utils.presentation.flowMVI.fastConfig

private typealias Ctx = PipelineContext<ManageTransactionState, ManageTransactionIntent, Nothing>

class ManageTransactionContainer(
    transactionId: String?,
) : Container<ManageTransactionState, ManageTransactionIntent, Nothing> {
    private val isCreateMode = transactionId == null

    override val store: Store<ManageTransactionState, ManageTransactionIntent, Nothing> =
        store(
            initial = ManageTransactionState.OK(
                ManageTransactionState.OK.FormState(
                    value = BigDecimal("0"),
                    transactionType = "",
                    category = "",
                    note = ""
                ),
                isCreateMode = isCreateMode,
            )
        ) {
            fastConfig(
                name = "ManageTransaction", resetOnStop = true,
                doOnRecover = { ManageTransactionState.FatalError(it.message.orUnknown) }
            )

            install(manageTransactionBasePlugin())


            reduce { intent ->
                withType<ManageTransactionState.OK, _> {
                    when (intent) {
                        ManageTransactionIntent.ClickedDelete -> if (!isCreateMode) {
                            TODO()
                        }

                        ManageTransactionIntent.ClickedSave -> if (isCreateMode) {
                            TODO()
                        } else {
                            TODO()
                        }
                    }
                }
            }

        }
}
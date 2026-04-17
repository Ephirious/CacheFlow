package editors.accounts.mvi

import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.plugin
import utils.presentation.flowMVI.customOnIntent
import utils.types.HexColor

@FlowMVIDSL
fun <S : MVIState, I : ManageAccountBaseIntent, A : MVIAction, F : ManageAccountFormBaseState<*>>
        manageAccountBasePlugin(
    getState: S.() -> ManageAccountBaseState<F>?,
    setState: S.(ManageAccountBaseState<F>) -> S,
    makeOK: (F) -> ManageAccountBaseState<F>,
) =
    plugin<S, I, A> {
        name = "ManageAccountBasePlugin"
        customOnIntent { intent ->
            val baseIntent = intent as? ManageAccountBaseIntent.Internal ?: return@customOnIntent intent
            updateState {
                val baseState = getState() ?: return@updateState this

                if (baseIntent is ManageAccountBaseIntent.ClickedTryAgain && baseState is ManageAccountBaseState.FatalError<F>) {
                    val lastForm = baseState.lastForm ?: TODO()
                    return@updateState setState(makeOK(lastForm))
                }

                if (baseState !is ManageAccountBaseState.OK<*>) return@updateState this

                val currentForm = baseState.form


                val updatedForm = when (baseIntent) {
                    is ManageAccountBaseIntent.ChangedColor -> {
                        @Suppress("UNCHECKED_CAST")
                        (currentForm.copyBase(color = HexColor(baseIntent.color)) as F)
//                            .validated(ManageAccountFormBaseValidationFields.color)
                    }

                    is ManageAccountBaseIntent.ChangedTitle -> {
                        currentForm.copyBase(title = baseIntent.title)
                    }

                    ManageAccountBaseIntent.ClickedTryAgain -> currentForm
                }

                @Suppress("UNCHECKED_CAST")
                setState(makeOK(updatedForm as F))
            }

            null
        }
    }
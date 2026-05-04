package editors.categories.mvi

import pro.respawn.flowmvi.api.FlowMVIDSL
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.dsl.plugin
import utils.presentation.flowMVI.customOnIntent

@FlowMVIDSL
fun <S : MVIState, I : ManageCategoryBaseIntent, A : MVIAction, F : ManageCategoryFormBaseState<ManageCategoryFormBaseValidationErrors>>
        manageCategoryBasePlugin(
    getState: S.() -> ManageCategoryBaseState<F>?,
    setState: S.(ManageCategoryBaseState<F>) -> S,
    makeOK: (F) -> ManageCategoryBaseState<F>,
) =
    plugin<S, I, A> {
        name = "ManageCategoryBasePlugin"
        customOnIntent { intent ->
            val baseIntent = intent as? ManageCategoryBaseIntent.Internal ?: return@customOnIntent intent
            updateState {
                val baseState = getState() ?: return@updateState this

                if (baseIntent is ManageCategoryBaseIntent.ClickedTryAgain && baseState is ManageCategoryBaseState.FatalError<F>) {
                    val lastForm = baseState.lastForm ?: TODO()
                    return@updateState setState(makeOK(lastForm))
                }

                if (baseState !is ManageCategoryBaseState.OK<*>) return@updateState this

                val currentForm = baseState.form


                val updatedForm = when (baseIntent) {
                    is ManageCategoryBaseIntent.ChangedEmoji -> {
                        @Suppress("UNCHECKED_CAST")
                        (currentForm.copyBase(emoji = baseIntent.emoji) as F)
                            .validated(ManageCategoryFormBaseValidationFields.emoji)
                    }

                    is ManageCategoryBaseIntent.ChangedName -> {
                        currentForm.copyBase(name = baseIntent.name)
                    }

                    ManageCategoryBaseIntent.ClickedTryAgain -> currentForm
                }

                @Suppress("UNCHECKED_CAST")
                setState(makeOK(updatedForm as F))
            }

            null
        }
    }
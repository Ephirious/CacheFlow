package editors.categories.mvi

import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import utils.annotations.DataCopyable
import utils.annotations.GenerateValidator
import utils.annotations.validation.MaxLen
import utils.annotations.validation.NotEmptyOrNullString
import kotlin.js.JsExport


@DataCopyable
interface ManageCategoryBaseState<F : ManageCategoryFormBaseState<*>> : MVIState {


    interface OK<F : ManageCategoryFormBaseState<*>> : ManageCategoryBaseState<F> {
        val form: F
    }

    interface FatalError<F : ManageCategoryFormBaseState<*>> : ManageCategoryBaseState<F> {
        val message: String
        val lastForm: F?
    }
}

@JsExport
@DataCopyable
@GenerateValidator
interface ManageCategoryFormBaseState<V> : MVIState {
    val name: String

    @MaxLen(1)
    @NotEmptyOrNullString
    val emoji: String
    val validation: V
}

@JsExport
sealed class ManageCategoryBaseIntent : MVIIntent {

    sealed interface Internal

    data class ChangedName(val name: String) : ManageCategoryBaseIntent(), Internal
    data class ChangedEmoji(val emoji: String) : ManageCategoryBaseIntent(), Internal
    data object ClickedTryAgain : ManageCategoryBaseIntent(), Internal
}
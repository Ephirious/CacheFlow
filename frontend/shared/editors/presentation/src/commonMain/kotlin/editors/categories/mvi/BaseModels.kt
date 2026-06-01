package editors.categories.mvi

import core_validation.GenerateValidator
import core_validation.data.category.CategoryEmojiValidator
import core_validation.data.category.CategoryTitleValidator
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import utils.annotations.DataCopyable
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

    @CategoryTitleValidator
    val title: String

    @CategoryEmojiValidator
    val emoji: String
    val validation: V
}

@JsExport
sealed class ManageCategoryBaseIntent : MVIIntent {

    sealed interface Internal

    data class ChangedTitle(val title: String) : ManageCategoryBaseIntent(), Internal
    data class ChangedEmoji(val emoji: String) : ManageCategoryBaseIntent(), Internal
    data object ClickedTryAgain : ManageCategoryBaseIntent(), Internal
}
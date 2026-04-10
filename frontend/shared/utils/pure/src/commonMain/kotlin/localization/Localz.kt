package localization

import utils.annotations.ValidationError
import kotlin.js.JsExport

@JsExport
sealed interface ValidationKey


@JsExport
sealed interface Localz {
    fun by(key: ValidationKey): String
    fun byError(error: ValidationError) = if (error is ValidationKey) by(error) else "unknown"
}

// TODO: dynamic change
@JsExport
val localz: Localz = RuLocalz()
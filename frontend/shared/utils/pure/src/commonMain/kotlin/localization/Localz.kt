package localization

import utils.CustomError
import kotlin.js.JsExport

@JsExport
sealed interface ValidationKey


@JsExport
sealed interface Localz {
    fun by(key: ValidationKey): String
    fun byValidation(error: CustomError) = if (error is ValidationKey) by(error) else "unknown"
}

// TODO: dynamic change
@JsExport
val localz: Localz = RuLocalz()
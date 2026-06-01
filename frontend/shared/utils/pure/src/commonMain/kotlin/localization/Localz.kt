package localization

import utils.CustomError
import kotlin.js.JsExport

@JsExport
sealed interface LocalzKey


@JsExport
sealed interface Localz {
    fun by(key: LocalzKey): String
    fun byValidation(error: CustomError) = if (error is LocalzKey) by(error) else "unknown"
}

// TODO: dynamic change
@JsExport
val localz: Localz = RuLocalz()
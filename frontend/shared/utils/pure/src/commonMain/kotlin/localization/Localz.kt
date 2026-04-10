package localization

import kotlin.js.JsExport

@JsExport
sealed interface ValidationKey


@JsExport
sealed interface Localz {
    fun get(key: ValidationKey): String
}

// TODO: dynamic change
@JsExport
val strings: Localz = RuLocalz()
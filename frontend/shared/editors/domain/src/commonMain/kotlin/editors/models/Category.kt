package editors.models

import kotlin.js.JsExport

@JsExport
data class Category(
    val id: String,
    val name: String,
    val emoji: String,
) {
    companion object {
        val Unknown = Category(
            id = "unknown",
            name = "Неизвестная категория",
            emoji = "❔"
        )
    }
}
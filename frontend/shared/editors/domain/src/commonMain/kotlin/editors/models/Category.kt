package editors.models

import dbEnums.CategoryType
import kotlin.js.JsExport

@JsExport
data class Category(
    val id: String,
    val name: String,
    val emoji: String,
    val type: CategoryType,
) {
    companion object {
        val Unknown = Category(
            id = "unknown",
            name = "Начальный баланс", // TODO
            emoji = "❔",
            type = CategoryType.INCOME
        )
    }
}
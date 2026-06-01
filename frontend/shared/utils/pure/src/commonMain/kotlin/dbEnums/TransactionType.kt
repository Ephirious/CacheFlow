package dbEnums

import kotlin.js.JsExport

@JsExport
@Suppress("EnumEntryName")
enum class TransactionTypeEnum {
    income, outcome, transfer
}
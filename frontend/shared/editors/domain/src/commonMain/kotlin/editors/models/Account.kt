package editors.models

import utils.types.BigDecimal
import utils.types.HexColor
import kotlin.js.JsExport

@JsExport
data class Account(
    val id: String,
    val title: String,
    val balance: BigDecimal,
    // TODO
    val color: HexColor = HexColor("#FF0000")
)

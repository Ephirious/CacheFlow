package interopSample.models

import utils.types.BigDecimal
import kotlin.js.JsExport

@JsExport
data class Weather(
    val temperature: BigDecimal,
    val temperatureUnit: String
)
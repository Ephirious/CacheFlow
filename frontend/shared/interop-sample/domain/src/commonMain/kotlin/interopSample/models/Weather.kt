package interopSample.models

import utils.BigDecimal
import kotlin.js.JsExport

@JsExport
data class Weather(
    val temperature: BigDecimal,
    val temperatureUnit: String
)
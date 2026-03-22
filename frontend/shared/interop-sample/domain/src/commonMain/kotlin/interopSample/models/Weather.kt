package interopSample.models

import utils.BigDecimal

@JsExport
data class Weather(
    val temperature: BigDecimal,
    val temperatureUnit: String
)
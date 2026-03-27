package utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.js.JsExport
import kotlin.time.Instant

val currentTimeZone = TimeZone.currentSystemDefault()

fun LocalDate.toInstant() = atStartOfDayIn(currentTimeZone)

fun Instant.toLocalDate() = toLocalDateTime(currentTimeZone).date


@JsExport
fun LocalDate.prettyDate(): String = toString().replace("-", ".")

@JsExport
fun LocalDateTime.isoString(): String = toString()
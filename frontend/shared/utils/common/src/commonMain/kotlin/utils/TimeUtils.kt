package utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.js.JsExport
import kotlin.time.Instant

val utcTimeZone = TimeZone.UTC

fun LocalDate.toInstant() = atStartOfDayIn(utcTimeZone)

fun Instant.toLocalDate() = toLocalDateTime(utcTimeZone).date


@JsExport
fun LocalDate.prettyDate(): String = toString().replace("-", ".")

@JsExport
fun LocalDateTime.isoString(): String = toString()
package core.sqldelight

import app.cash.sqldelight.ColumnAdapter
import utils.types.BigDecimal
import kotlin.time.Instant

val bigDecimalAdapter = object : ColumnAdapter<BigDecimal, String> {
    override fun decode(databaseValue: String): BigDecimal = BigDecimal(databaseValue)
    override fun encode(value: BigDecimal): String = value.toString()
}

val instantAdapter = object : ColumnAdapter<Instant, String> {
    override fun decode(databaseValue: String): Instant {
        return Instant.parse(databaseValue)
    }

    override fun encode(value: Instant): String {
        // iso 8601
        return value.toString()
    }
}
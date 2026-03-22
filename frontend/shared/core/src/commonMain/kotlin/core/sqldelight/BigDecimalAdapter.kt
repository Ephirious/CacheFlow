package core.sqldelight

import app.cash.sqldelight.ColumnAdapter
import utils.BigDecimal

val bigDecimalAdapter = object : ColumnAdapter<BigDecimal, String> {
    override fun decode(databaseValue: String): BigDecimal = BigDecimal(databaseValue)
    override fun encode(value: BigDecimal): String = value.toString()
}
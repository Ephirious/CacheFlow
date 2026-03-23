package utils.bigDecimalExtensions

import utils.BigDecimal

operator fun BigDecimal.plus(other: Number): BigDecimal = this.plus(BigDecimal(other))
operator fun BigDecimal.minus(other: Number): BigDecimal = this.minus(BigDecimal(other))
operator fun BigDecimal.times(other: Number): BigDecimal = this.times(BigDecimal(other))
operator fun BigDecimal.div(other: Number): BigDecimal = this.div(BigDecimal(other))


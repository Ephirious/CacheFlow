package utils.bigDecimalExtensions

import utils.BigDecimal


operator fun BigDecimal.plus(other: BigDecimal): BigDecimal = this.plus(other)
operator fun BigDecimal.minus(other: BigDecimal): BigDecimal = this.minus(other)
operator fun BigDecimal.times(other: BigDecimal): BigDecimal = this.multiply(other)
operator fun BigDecimal.div(other: BigDecimal): BigDecimal = this.divide(other)

operator fun BigDecimal.plus(other: Int): BigDecimal = this.plus(BigDecimal(other.toString()))
operator fun BigDecimal.minus(other: Int): BigDecimal = this.minus(BigDecimal(other.toString()))
operator fun BigDecimal.times(other: Int): BigDecimal = this.multiply(BigDecimal(other.toString()))
operator fun BigDecimal.div(other: Int): BigDecimal = this.divide(BigDecimal(other.toString()))


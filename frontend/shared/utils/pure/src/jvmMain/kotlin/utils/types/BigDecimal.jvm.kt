package utils.types

import kotlinx.serialization.Serializable


@Serializable(with = BigDecimalSerializer::class)
actual class BigDecimal : Comparable<BigDecimal> {
    actual operator fun plus(other: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    actual operator fun minus(other: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    actual operator fun times(other: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    actual operator fun div(other: BigDecimal): BigDecimal {
        TODO("Not yet implemented")
    }

    actual fun formattedString(dp: Int): String {
        TODO("Not yet implemented")
    }

    actual override fun toString(): String {
        TODO("Not yet implemented")
    }

    actual fun isGreater(other: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isLower(other: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }

    actual fun eq(other: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }

    actual val isPositive: Boolean
        get() = TODO("Not yet implemented")
    actual val isNegative: Boolean
        get() = TODO("Not yet implemented")
    actual val isZero: Boolean
        get() = TODO("Not yet implemented")

    actual fun abs(): BigDecimal {
        TODO("Not yet implemented")
    }

    actual override operator fun compareTo(other: BigDecimal): Int {
        TODO("Not yet implemented")
    }

    actual constructor(value: String) {
        TODO("Not yet implemented")
    }


    actual constructor(value: Number) {
        TODO("Not yet implemented")
    }
}
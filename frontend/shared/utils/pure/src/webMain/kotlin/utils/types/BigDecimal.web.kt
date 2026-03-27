package utils.types

import kotlinx.serialization.Serializable

@JsModule("big.js")
@JsNonModule
@JsName("Big")
external class BigJs(value: dynamic) {
    fun plus(other: BigJs): BigJs
    fun minus(other: BigJs): BigJs
    fun times(other: BigJs): BigJs
    fun div(other: BigJs): BigJs

    fun gt(other: BigJs): Boolean
    fun lt(other: BigJs): Boolean
    fun eq(other: BigJs): Boolean

    fun cmp(other: BigJs): Int
    fun abs(): BigJs


    fun toFixed(dp: Int): String

    override fun toString(): String
}

@JsExport
@Serializable(with = BigDecimalSerializer::class)
actual class BigDecimal(
    internal val internal: BigJs,
) : Comparable<BigDecimal> {

    @JsName("from")
    actual constructor(value: String) : this(BigJs(value))


    @JsExport.Ignore
    actual constructor(value: Number) : this(BigJs(value))

    actual companion object {
        val ZERO_BIGJS = BigJs(0)
    }


    actual operator fun plus(other: BigDecimal) = BigDecimal(internal.plus(other.internal))
    actual operator fun minus(other: BigDecimal) = BigDecimal(internal.minus(other.internal))
    actual operator fun times(other: BigDecimal) = BigDecimal(internal.times(other.internal))
    actual operator fun div(other: BigDecimal) = BigDecimal(internal.div(other.internal))

    actual fun formattedString(dp: Int): String = internal.toFixed(dp)
    actual override fun toString(): String = internal.toString()

    actual fun isGreater(other: BigDecimal): Boolean = internal.gt(other.internal)

    actual fun isLower(other: BigDecimal): Boolean = internal.lt(other.internal)

    actual fun eq(other: BigDecimal): Boolean = internal.eq(other.internal)

    actual val isPositive: Boolean
        get() = internal.gt(ZERO_BIGJS)
    actual val isNegative: Boolean
        get() = internal.lt(ZERO_BIGJS)
    actual val isZero: Boolean
        get() = internal.eq(ZERO_BIGJS)

    actual fun abs(): BigDecimal = BigDecimal(internal.abs())

    actual override operator fun compareTo(other: BigDecimal): Int {
        return internal.cmp(other.internal)
    }
}

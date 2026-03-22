package utils

import kotlinx.serialization.Serializable

@JsModule("big.js")
@JsNonModule
@JsName("Big")
external class BigJs(value: dynamic) {
    fun plus(other: BigJs): BigJs
    fun minus(other: BigJs): BigJs
    fun times(other: BigJs): BigJs
    fun div(other: BigJs): BigJs
    fun toFixed(dp: Int): String

    override fun toString(): String
}

@JsExport
@Serializable(with = BigDecimalSerializer::class)
actual class BigDecimal actual constructor(value: String) {
    @JsName(name = "BigDecimalFromDouble")
    actual constructor(value: Double) : this(value.toString())

    @JsName(name = "BigDecimalFromInt")
    actual constructor(value: Int) : this(value.toString())


    private val internal = BigJs(value)


    actual fun plus(other: BigDecimal) = BigDecimal(internal.plus(other.internal).toString())
    actual fun minus(other: BigDecimal) = BigDecimal(internal.minus(other.internal).toString())
    actual fun multiply(other: BigDecimal) = BigDecimal(internal.times(other.internal).toString())
    actual fun divide(other: BigDecimal) = BigDecimal(internal.div(other.internal).toString())

    actual fun toFormattedString(dp: Int): String = internal.toFixed(dp)
    actual override fun toString(): String = internal.toString()

}
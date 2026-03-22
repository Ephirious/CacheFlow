package utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

@JsExport
@Serializable(with = BigDecimalSerializer::class)
expect class BigDecimal : Comparable<BigDecimal> {

    @JsName("from")
    constructor(value: String)

    @JsExport.Ignore
    constructor(value: Number)


    operator fun plus(other: BigDecimal): BigDecimal
    operator fun minus(other: BigDecimal): BigDecimal
    operator fun times(other: BigDecimal): BigDecimal
    operator fun div(other: BigDecimal): BigDecimal
    fun formattedString(dp: Int): String

    override fun toString(): String

    fun isGreater(other: BigDecimal): Boolean
    fun isLower(other: BigDecimal): Boolean
    fun eq(other: BigDecimal): Boolean
    val isPositive: Boolean
    val isNegative: Boolean
    val isZero: Boolean
    fun abs(): BigDecimal

    override fun compareTo(other: BigDecimal): Int
}

object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement() as JsonPrimitive
            BigDecimal(element.content)
        } else {
            BigDecimal(decoder.decodeString())
        }
    }
}
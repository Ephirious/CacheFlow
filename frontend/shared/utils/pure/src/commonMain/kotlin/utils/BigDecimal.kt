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
expect class BigDecimal(value: String) {

    @JsName("BigDecimalFromDouble")
    constructor(value: Double)

    @JsName("BigDecimalFromInt")
    constructor(value: Int)

    fun plus(other: BigDecimal): BigDecimal
    fun minus(other: BigDecimal): BigDecimal
    fun multiply(other: BigDecimal): BigDecimal
    fun divide(other: BigDecimal): BigDecimal
    fun toFormattedString(dp: Int): String
    override fun toString(): String
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
package manageTransaction.validation

import manageTransaction.validation.StringAmountError.*
import utils.annotations.LinkedRule
import utils.annotations.ValidationError
import utils.annotations.ValidationRule
import utils.types.BigDecimal
import kotlin.js.JsExport


@JsExport
sealed class StringAmountError : ValidationError {
    data object EmptyAmount : StringAmountError()
    data object NotANumber : StringAmountError()
    data object NotPositive : StringAmountError()
    data object ScaleExceeded : StringAmountError()
    data object TooMuch : StringAmountError()
}

object StringAmountRule : ValidationRule<String, StringAmountError> {
    override fun validate(value: String, param: Any?): StringAmountError? {
        if (value.isBlank()) return EmptyAmount

        val decimal = try {
            BigDecimal(value)
        } catch (_: Throwable) {
            return NotANumber
        }

        if (decimal <= BigDecimal.ZERO) {
            return NotPositive
        }

        val currentScale = if (value.contains(".")) {
            value.substringAfter(".").length
        } else {
            0
        }

        if (currentScale > 2) {
            return ScaleExceeded
        }

        if (decimal > BigDecimal("1000000000")) {
            return TooMuch
        }

        return null
    }
}

@LinkedRule(StringAmountRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class StringAmount

package utils.annotations.validation

import localization.StringAmountError
import localization.StringAmountError.*
import utils.annotations.LinkedRule
import utils.annotations.ValidationRule
import utils.types.BigDecimal

object StringAmountRule : ValidationRule<String, Any, Boolean, StringAmountError> {
    override fun validate(value: String, ctx: Any, param: Boolean): StringAmountError? {

        if (value.isBlank()) return EmptyAmount

        val decimal = try {
            BigDecimal(value)
        } catch (_: Throwable) {
            return NotANumber
        }

        // param = mustBePositive
        if (param && decimal <= BigDecimal.ZERO) {
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

//        if (decimal > BigDecimal("1000000000")) {
//            return TooMuch
//        }

        return null
    }
}

@LinkedRule(StringAmountRule::class)
@Target(AnnotationTarget.PROPERTY)
// param – must be positive
annotation class StringAmount(@Suppress("unused")  val param: Boolean)

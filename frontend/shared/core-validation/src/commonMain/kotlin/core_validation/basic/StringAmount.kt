package core_validation.basic

import core_validation.LinkedRule
import core_validation.ValidationRule
import localization.StringAmountError
import localization.StringAmountError.*
import utils.types.BigDecimal

object StringAmountRule : ValidationRule<String, Any, Boolean, StringAmountError> {
    override fun validateKSP(value: String, ctx: Any, param: Boolean): StringAmountError? =
        validate(value, param)

    fun validate(amount: String, mustBePositive: Boolean): StringAmountError? {

        if (amount.isBlank()) return EmptyAmount

        val decimal = try {
            BigDecimal(amount)
        } catch (_: Throwable) {
            return NotANumber
        }

        if (mustBePositive && decimal <= BigDecimal.ZERO) {
            return NotPositive
        }

        val currentScale = if (amount.contains(".")) {
            amount.substringAfter(".").length
        } else {
            0
        }

        if (currentScale > 2) {
            return ScaleExceeded
        }

        return null
    }
}

@LinkedRule(StringAmountRule::class)
@Target(AnnotationTarget.PROPERTY)
// param – must be positive
annotation class StringAmount(@Suppress("unused") val param: Boolean)

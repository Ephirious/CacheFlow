package manageTransaction.validationRules

import utils.annotations.LinkedRule
import utils.annotations.ValidationRule
import utils.types.BigDecimal

object StringAmountRule : ValidationRule<String> {
    override fun validate(value: String, param: Any?): String? {
        if (value.isBlank()) return "Сумма не может быть пустой"

        val decimal = try {
            BigDecimal(value)
        } catch (e: Throwable) {
            return "Это не похоже на число"
        }

        if (decimal <= BigDecimal.ZERO) {
            return "Сумма должна быть больше нуля"
        }

        val currentScale = if (value.contains(".")) {
            value.substringAfter(".").length
        } else {
            0
        }

        if (currentScale > 2) {
            return "Максимум 2 знака после запятой"
        }

        if (decimal > BigDecimal("1000000000")) {
            return "Слишком большая сумма вау (лимит 1 млрд)"
        }

        return null
    }
}

@LinkedRule(StringAmountRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class StringAmount

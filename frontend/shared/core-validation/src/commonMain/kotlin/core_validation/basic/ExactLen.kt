package core_validation.basic

import core_validation.LinkedRule
import core_validation.ValidationRule
import localization.LenError

object ExactLenRule : ValidationRule<String, Any, Int, LenError> {
    override fun validate(value: String, ctx: Any, param: Int): LenError? {
        return if (value.unicodeLength() != param) {
            LenError.NotExactLength(param)
        } else null
    }
}

@LinkedRule(ExactLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class ExactLen(@Suppress("unused") val param: Int)
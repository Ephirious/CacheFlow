package utils.annotations.validation

import localization.MaxLenError
import localization.MaxLenError.MaxLengthExceeded
import utils.annotations.LinkedRule
import utils.annotations.ValidationRule

object MaxLenRule : ValidationRule<String, Any, Int, MaxLenError> {
    override fun validate(value: String, ctx: Any, param: Int): MaxLenError? {
        return if (value.length > param) {
            MaxLengthExceeded(param)
        } else null
    }
}

@LinkedRule(MaxLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxLen(@Suppress("unused") val param: Int)



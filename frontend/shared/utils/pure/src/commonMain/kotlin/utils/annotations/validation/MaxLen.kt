package utils.annotations.validation

import localization.MaxLenError
import localization.MaxLenError.MaxLengthExceeded
import utils.annotations.LinkedRule
import utils.annotations.ValidationRule

object MaxLenRule : ValidationRule<String, Any, MaxLenError> {
    override fun validate(value: String, state: Any, param: Any?): MaxLenError? {
        return if (value.length > (param as Int)) {
            MaxLengthExceeded(param)
        } else null
    }
}

@LinkedRule(MaxLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxLen(@Suppress("unused") val param: Int)



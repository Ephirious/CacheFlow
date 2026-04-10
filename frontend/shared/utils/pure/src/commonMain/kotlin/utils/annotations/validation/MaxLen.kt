package utils.annotations.validation

import utils.annotations.LinkedRule
import utils.annotations.ValidationError
import utils.annotations.ValidationRule
import utils.annotations.validation.MaxLenError.*

sealed class MaxLenError : ValidationError {
    data class MaxLengthExceeded(val len: Int) : MaxLenError()
}

object MaxLenRule : ValidationRule<String, MaxLenError> {
    override fun validate(value: String, param: Any?): MaxLenError? {
        return if (value.length > (param as Int)) {
            MaxLengthExceeded(param)
        } else null
    }
}

@LinkedRule(MaxLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxLen(val param: Int)



package core_validation.basic

import core_validation.LinkedRule
import core_validation.ValidationRule
import localization.LenError
import utils.visualLength


object MaxLenRule : ValidationRule<String, Any, Int, LenError> {
    override fun validateKSP(value: String, ctx: Any, param: Int): LenError? =
        validate(value, param)

    fun validate(str: String, maxLen: Int): LenError? {
        return if (str.visualLength() > maxLen) {
            LenError.MaxLengthExceeded(maxLen)
        } else null
    }
}

@LinkedRule(MaxLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxLen(@Suppress("unused") val param: Int)



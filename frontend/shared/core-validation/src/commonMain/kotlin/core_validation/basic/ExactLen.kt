package core_validation.basic

import core_validation.LinkedRule
import core_validation.ValidationRule
import localization.LenError
import utils.visualLength

object ExactLenRule : ValidationRule<String, Any, Int, LenError> {
    override fun validateKSP(value: String, ctx: Any, param: Int): LenError? =
        validate(value, param)

    fun validate(str: String, exactLen: Int): LenError? {
        return if (str.visualLength() != exactLen) {
            LenError.NotExactLength(exactLen)
        } else null
    }
}

@LinkedRule(ExactLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class ExactLen(@Suppress("unused") val param: Int)
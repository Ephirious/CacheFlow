package core_validation.data.auth

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.ExactLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object AuthOTPCodeRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(otp: String) =
        combineRules(
            { NotEmptyOrNullStringRule.validate(otp) },
            { ExactLenRule.validate(otp, exactLen = 6) }
        )
}

@LinkedRule(AuthOTPCodeRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AuthOTPCodeValidator
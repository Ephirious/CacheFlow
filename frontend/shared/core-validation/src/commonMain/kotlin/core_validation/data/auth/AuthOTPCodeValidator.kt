package core_validation.data.auth

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.ExactLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object AuthOTPCodeRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validate(value: String, ctx: Any, param: Nothing?): CustomError? {
        return combineRules(
            { NotEmptyOrNullStringRule.validate(value, ctx, param) },
            // param = exactLen
            { ExactLenRule.validate(value, ctx, param = 6) }
        )
    }
}

@LinkedRule(AuthOTPCodeRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AuthOTPCodeValidator
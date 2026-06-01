package core_validation.data.auth

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object AuthUsernameRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validate(value: String, ctx: Any, param: Nothing?): CustomError? {
        return combineRules(
            value, ctx,
            { NotEmptyOrNullStringRule.validate(value, ctx, param) },
            // param = maxLen
            { MaxLenRule.validate(value, ctx, param = 100) }
        )
    }
}

@LinkedRule(AuthUsernameRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AuthUsernameValidator
package core_validation.data.auth

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import core_validation.data.auth.internal.EmailFormatRule
import utils.CustomError

object AuthEmailRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validate(value: String, ctx: Any, param: Nothing?): CustomError? {
        return combineRules(
            { NotEmptyOrNullStringRule.validate(value, ctx, param) },
            { EmailFormatRule.validate(value, ctx, param) },
            // param = maxLen
            { MaxLenRule.validate(value, ctx, param = 255) }
        )
    }
}

@LinkedRule(AuthEmailRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AuthEmailValidator
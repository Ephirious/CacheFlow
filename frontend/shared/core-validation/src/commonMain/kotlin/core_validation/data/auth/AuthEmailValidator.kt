package core_validation.data.auth

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import core_validation.data.auth.internal.EmailInternalFormatRule
import utils.CustomError

object AuthEmailRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(email: String) =
        combineRules(
            { NotEmptyOrNullStringRule.validate(email) },
            { EmailInternalFormatRule.validate(email) },
            { MaxLenRule.validate(email, maxLen = 255) }
        )
}

@LinkedRule(AuthEmailRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AuthEmailValidator
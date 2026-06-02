package core_validation.data.auth

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object AuthUsernameRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(username: String) =
        combineRules(
            { NotEmptyOrNullStringRule.validate(username) },
            { MaxLenRule.validate(username, maxLen = 100) }
        )
}

@LinkedRule(AuthUsernameRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AuthUsernameValidator
package core_validation.data.auth

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object AuthPasswordRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(password: String) =
        combineRules(
            { NotEmptyOrNullStringRule.validate(password) }
        )
}

@LinkedRule(AuthPasswordRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AuthPasswordValidator
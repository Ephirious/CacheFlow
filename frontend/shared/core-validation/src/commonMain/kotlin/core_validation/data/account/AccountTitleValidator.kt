package core_validation.data.account

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object AccountTitleRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(title: String) =
        combineRules(
            { MaxLenRule.validate(title, maxLen = 100) },
            { NotEmptyOrNullStringRule.validate(title) }
        )
}

@LinkedRule(AccountTitleRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AccountTitleValidator
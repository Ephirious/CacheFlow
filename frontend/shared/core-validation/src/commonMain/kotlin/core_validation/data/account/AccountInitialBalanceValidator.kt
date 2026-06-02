package core_validation.data.account

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.StringAmountRule
import core_validation.combineRules
import utils.CustomError

object AccountInitialBalanceRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(initialBalance: String): CustomError? =
        combineRules(
            { StringAmountRule.validate(initialBalance, mustBePositive = false) },
        )

}

@LinkedRule(AccountInitialBalanceRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AccountInitialBalanceValidator
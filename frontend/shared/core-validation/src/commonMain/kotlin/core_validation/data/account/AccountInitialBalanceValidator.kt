package core_validation.data.account

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.StringAmountRule
import core_validation.combineRules
import utils.CustomError

object AccountInitialBalanceRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validate(value: String, ctx: Any, param: Nothing?): CustomError? {
        return combineRules(
            value, ctx,
            // param = must be positive
            { StringAmountRule.validate(value, ctx, param = false) },
        )
    }
}

@LinkedRule(AccountInitialBalanceRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class AccountInitialBalanceValidator
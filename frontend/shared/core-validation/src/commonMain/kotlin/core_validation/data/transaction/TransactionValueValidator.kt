package core_validation.data.transaction

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.StringAmountRule
import core_validation.combineRules
import utils.CustomError

object TransactionValueRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validate(value: String, ctx: Any, param: Nothing?): CustomError? {
        return combineRules(
            // param = must be positive
            { StringAmountRule.validate(value, ctx, param = true) },
            // param = maxLen
            { MaxLenRule.validate(value, ctx, param = 10) }
        )
    }
}

@LinkedRule(TransactionValueRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class TransactionValueValidator
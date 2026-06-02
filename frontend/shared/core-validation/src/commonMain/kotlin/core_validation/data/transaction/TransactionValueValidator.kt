package core_validation.data.transaction

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.StringAmountRule
import core_validation.combineRules
import utils.CustomError

object TransactionValueRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(value: String) =
        combineRules(
            { StringAmountRule.validate(value, mustBePositive = true) },
            { MaxLenRule.validate(value, maxLen = 10) }
        )
}

@LinkedRule(TransactionValueRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class TransactionValueValidator
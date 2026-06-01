package core_validation.data.transaction

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.combineRules
import utils.CustomError

object TransactionNoteRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validate(value: String, ctx: Any, param: Nothing?): CustomError? {
        return combineRules(
            // param = maxLen
            { MaxLenRule.validate(value, ctx, param = 1024) }
        )
    }
}

@LinkedRule(TransactionNoteRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class TransactionNoteValidator
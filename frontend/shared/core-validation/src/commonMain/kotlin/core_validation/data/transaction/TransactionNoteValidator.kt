package core_validation.data.transaction

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.combineRules
import utils.CustomError

object TransactionNoteRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(note: String) =
        combineRules(
            { MaxLenRule.validate(note, maxLen = 1024) }
        )
}

@LinkedRule(TransactionNoteRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class TransactionNoteValidator
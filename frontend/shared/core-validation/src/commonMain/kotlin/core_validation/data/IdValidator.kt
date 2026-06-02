package core_validation.data

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object IdRule : ValidationRule<String?, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String?, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(id: String?) = combineRules(
        { NotEmptyOrNullStringRule.validate(id) }
    )
}

@LinkedRule(IdRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class IdValidator
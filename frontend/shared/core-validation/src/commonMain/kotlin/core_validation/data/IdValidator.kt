package core_validation.data

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object IdRule : ValidationRule<String?, Any, Nothing?, CustomError> {
    override fun validate(value: String?, ctx: Any, param: Nothing?): CustomError? {
        return combineRules(
            value, ctx,
            { NotEmptyOrNullStringRule.validate(value, ctx, param) }
        )
    }
}

@LinkedRule(IdRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class IdValidator
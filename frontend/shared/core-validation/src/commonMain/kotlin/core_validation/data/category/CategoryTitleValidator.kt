package core_validation.data.category

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object CategoryTitleRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validate(value: String, ctx: Any, param: Nothing?): CustomError? {
        return combineRules(
            // param = максимальная длинна
            { MaxLenRule.validate(value, ctx, param = 100) },
            { NotEmptyOrNullStringRule.validate(value, ctx, param) }
        )
    }
}

@LinkedRule(CategoryTitleRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class CategoryTitleValidator
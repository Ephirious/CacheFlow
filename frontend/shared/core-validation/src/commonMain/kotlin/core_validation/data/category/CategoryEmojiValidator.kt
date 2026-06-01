package core_validation.data.category

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object CategoryEmojiRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validate(value: String, ctx: Any, param: Nothing?): CustomError? {
        return combineRules(
            value, ctx,
            // param = максимальная длинна
            { MaxLenRule.validate(value, ctx, param = 1) },
            { NotEmptyOrNullStringRule.validate(value, ctx, param) }
        )
    }
}

@LinkedRule(CategoryEmojiRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class CategoryEmojiValidator
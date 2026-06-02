package core_validation.data.category

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.basic.MaxLenRule
import core_validation.basic.NotEmptyOrNullStringRule
import core_validation.combineRules
import utils.CustomError

object CategoryEmojiRule : ValidationRule<String, Any, Nothing?, CustomError> {
    override fun validateKSP(value: String, ctx: Any, param: Nothing?): CustomError? =
        validate(value)

    fun validate(emoji: String) =
        combineRules(
            { MaxLenRule.validate(emoji, maxLen = 1) },
            { NotEmptyOrNullStringRule.validate(emoji) }
        )
}

@LinkedRule(CategoryEmojiRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class CategoryEmojiValidator
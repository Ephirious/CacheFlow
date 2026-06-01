package core_validation.basic

import core_validation.LinkedRule
import core_validation.ValidationRule
import localization.NotEmptyOrNullStringError

object NotEmptyOrNullStringRule : ValidationRule<String?, Any, Nothing?, NotEmptyOrNullStringError> {
    override fun validate(value: String?, ctx: Any, param: Nothing?): NotEmptyOrNullStringError? {
        return if (value.isNullOrEmpty()) {
            NotEmptyOrNullStringError.EmptyOrNullString
        } else null
    }
}

@LinkedRule(NotEmptyOrNullStringRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class NotEmptyOrNullString
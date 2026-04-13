package utils.annotations.validation

import localization.NotEmptyOrNullStringError
import utils.annotations.LinkedRule
import utils.annotations.ValidationRule

object NotEmptyOrNullStringRule : ValidationRule<String?, Any, NotEmptyOrNullStringError> {
    override fun validate(value: String?, state: Any, param: Any?): NotEmptyOrNullStringError? {
        return if (value.isNullOrEmpty()) {
            NotEmptyOrNullStringError.EmptyOrNullString
        } else null
    }
}

@LinkedRule(NotEmptyOrNullStringRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class NotEmptyOrNullString
package utils.annotations.validation

import utils.annotations.LinkedRule
import utils.annotations.ValidationRule

object MaxLenRule : ValidationRule<String> {
    override fun validate(value: String, param: Any?): String? {
        return if (value.length > (param as Int)) {
            "Превышена длина (макс. $param)"
        } else null
    }
}

@LinkedRule(MaxLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxLen(val param: Int)



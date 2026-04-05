package utils.annotations.validation

import utils.annotations.LinkedRule
import utils.annotations.ValidationRule

object MaxLenRule : ValidationRule<String> {
    override fun validate(value: String, param: Any?) = value.length <= (param as Int)
    override fun errorMessage(param: Any?) = "Превышена длина (макс. $param)"
}

@LinkedRule(MaxLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxLen(val param: Int, val message: String = "")


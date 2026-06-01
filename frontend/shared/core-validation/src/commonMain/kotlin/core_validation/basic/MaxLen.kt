package core_validation.basic

import core_validation.LinkedRule
import core_validation.ValidationRule
import localization.MaxLenError


private val unicodeRegex = Regex("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]|.")

private fun String.unicodeLength(): Int {
    return unicodeRegex.findAll(this).count()
}

object MaxLenRule : ValidationRule<String, Any, Int, MaxLenError> {
    override fun validate(value: String, ctx: Any, param: Int): MaxLenError? {
        return if (value.unicodeLength() > param) {
            MaxLenError.MaxLengthExceeded(param)
        } else null
    }
}

@LinkedRule(MaxLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxLen(@Suppress("unused") val param: Int)



package utils.annotations.validation

import localization.MaxLenError
import localization.MaxLenError.MaxLengthExceeded
import utils.annotations.LinkedRule
import utils.annotations.ValidationRule

private val unicodeRegex = Regex("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]|.")

fun String.unicodeLength(): Int {
    return unicodeRegex.findAll(this).count()
}

object MaxLenRule : ValidationRule<String, Any, Int, MaxLenError> {
    override fun validate(value: String, ctx: Any, param: Int): MaxLenError? {
        return if (value.unicodeLength() > param) {
            MaxLengthExceeded(param)
        } else null
    }
}

@LinkedRule(MaxLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxLen(@Suppress("unused") val param: Int)



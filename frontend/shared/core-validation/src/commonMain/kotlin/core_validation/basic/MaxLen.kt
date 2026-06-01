package core_validation.basic

import core_validation.LinkedRule
import core_validation.ValidationRule
import localization.LenError


private val unicodeRegex = Regex("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]|.")

internal fun String.unicodeLength(): Int {
    return unicodeRegex.findAll(this).count()
}

object MaxLenRule : ValidationRule<String, Any, Int, LenError> {
    override fun validate(value: String, ctx: Any, param: Int): LenError? {
        return if (value.unicodeLength() > param) {
            LenError.MaxLengthExceeded(param)
        } else null
    }
}

@LinkedRule(MaxLenRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class MaxLen(@Suppress("unused") val param: Int)



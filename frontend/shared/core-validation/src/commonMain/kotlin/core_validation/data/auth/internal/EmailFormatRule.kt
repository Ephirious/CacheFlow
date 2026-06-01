package core_validation.data.auth.internal

import core_validation.ValidationRule
import localization.EmailFormatError

object EmailFormatRule : ValidationRule<String, Any, Nothing?, EmailFormatError> {
    private val emailRegex = Regex(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    )

    override fun validate(value: String, ctx: Any, param: Nothing?): EmailFormatError? {
        return if (!emailRegex.matches(value)) {
            EmailFormatError.InvalidFormat
        } else null
    }
}
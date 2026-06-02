package core_validation.data.auth.internal

import core_validation.ValidationRule
import localization.EmailFormatError

object EmailInternalFormatRule : ValidationRule<String, Any, Nothing?, EmailFormatError> {
    private val emailRegex = Regex(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    )

    override fun validateKSP(value: String, ctx: Any, param: Nothing?): EmailFormatError? =
        validate(value)

    fun validate(email: String): EmailFormatError? {
        return if (!emailRegex.matches(email)) {
            EmailFormatError.InvalidFormat
        } else null
    }
}
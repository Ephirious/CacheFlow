package localization

import utils.annotations.ValidationError


enum class StringAmountError : ValidationError, ValidationKey {
    EmptyAmount, NotANumber, NotPositive, ScaleExceeded
}

sealed class MaxLenError : ValidationError, ValidationKey {
    data class MaxLengthExceeded(val limit: Int) : MaxLenError()
}

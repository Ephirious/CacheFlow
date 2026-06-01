package localization

import utils.CustomError


enum class StringAmountError : CustomError, ValidationKey {
    EmptyAmount, NotANumber, NotPositive, ScaleExceeded
}

enum class DiffTransferAccountsError : CustomError, ValidationKey {
    SameAccounts
}

sealed class MaxLenError : CustomError, ValidationKey {
    data class MaxLengthExceeded(val limit: Int) : MaxLenError()
}

enum class NotEmptyOrNullStringError : CustomError, ValidationKey {
    EmptyOrNullString
}

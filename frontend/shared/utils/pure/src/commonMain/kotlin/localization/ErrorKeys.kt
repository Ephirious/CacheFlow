package localization

import utils.CustomError


enum class StringAmountError : CustomError, ValidationKey {
    EmptyAmount, NotANumber, NotPositive, ScaleExceeded
}

enum class DiffTransferAccountsError : CustomError, ValidationKey {
    SameAccounts
}

sealed class LenError : CustomError, ValidationKey {
    data class MaxLengthExceeded(val limit: Int) : LenError()
    data class NotExactLength(val shouldBe: Int) : LenError()
}

enum class NotEmptyOrNullStringError : CustomError, ValidationKey {
    EmptyOrNullString
}


enum class EmailFormatError : CustomError, ValidationKey {
    InvalidFormat
}
package localization

import utils.CustomError


enum class StringAmountError : CustomError, LocalzKey {
    EmptyAmount, NotANumber, NotPositive, ScaleExceeded
}

enum class DiffTransferAccountsError : CustomError, LocalzKey {
    SameAccounts
}

sealed class LenError : CustomError, LocalzKey {
    data class MaxLengthExceeded(val limit: Int) : LenError()
    data class NotExactLength(val shouldBe: Int) : LenError()
}

enum class NotEmptyOrNullStringError : CustomError, LocalzKey {
    EmptyOrNullString
}


enum class EmailFormatError : CustomError, LocalzKey {
    InvalidFormat
}

enum class HardCodedServerError : LocalzKey {
    EmailAlreadyInUse, WrongLoginOrPassword, FailToFetch
}
package core_validation.data.transaction.internal

import core_validation.ValidationRule
import localization.DiffTransferAccountsError
import localization.DiffTransferAccountsError.SameAccounts


interface TransferAccountsContext {
    val fromId: String?
    val toId: String?
}

internal object DiffTransferAccountsRule :
    ValidationRule<String?, TransferAccountsContext, Nothing?, DiffTransferAccountsError> {
    override fun validate(value: String?, ctx: TransferAccountsContext, param: Nothing?): DiffTransferAccountsError? {
        if (ctx.fromId == ctx.toId) return SameAccounts
        return null
    }
}
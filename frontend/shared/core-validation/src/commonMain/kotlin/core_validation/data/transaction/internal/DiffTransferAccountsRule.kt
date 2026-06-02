package core_validation.data.transaction.internal

import core_validation.ValidationRule
import localization.DiffTransferAccountsError
import localization.DiffTransferAccountsError.SameAccounts
import kotlin.js.JsExport


@JsExport
interface TransferAccountsContext {
    val fromId: String?
    val toId: String?
}

object DiffTransferAccountsRule :
    ValidationRule<String?, TransferAccountsContext, Nothing?, DiffTransferAccountsError> {
    override fun validateKSP(
        value: String?,
        ctx: TransferAccountsContext,
        param: Nothing?
    ): DiffTransferAccountsError? =
        validate(fromId = ctx.fromId, toId = ctx.toId)

    fun validate(fromId: String?, toId: String?): DiffTransferAccountsError? {
        if (fromId == toId) return SameAccounts
        return null
    }
}
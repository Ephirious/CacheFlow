package manageTransaction.validation

import localization.DiffTransferAccountsError
import localization.DiffTransferAccountsError.SameAccounts
import manageTransaction.mvi.ManageTransactionType.Transfer
import utils.annotations.LinkedRule
import utils.annotations.ValidationRule

object DiffTransferAccountsRule : ValidationRule<String?, Transfer, Nothing?, DiffTransferAccountsError> {
    override fun validate(value: String?, ctx: Transfer, param: Nothing?): DiffTransferAccountsError? {
        if (ctx.fromId == ctx.toId) return SameAccounts
        return null
    }
}

@LinkedRule(DiffTransferAccountsRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class DiffTransferAccounts
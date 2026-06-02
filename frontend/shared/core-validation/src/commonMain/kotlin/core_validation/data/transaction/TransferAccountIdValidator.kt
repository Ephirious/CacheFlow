package core_validation.data.transaction

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.combineRules
import core_validation.data.IdRule
import core_validation.data.transaction.internal.DiffTransferAccountsRule
import core_validation.data.transaction.internal.TransferAccountsContext
import utils.CustomError

object TransferAccountIdRule : ValidationRule<String?, TransferAccountsContext, Nothing?, CustomError> {
    override fun validateKSP(value: String?, ctx: TransferAccountsContext, param: Nothing?): CustomError? =
        validate(value, fromId = ctx.fromId, toId = param)

    fun validate(accountId: String?, fromId: String?, toId: String?) =
        combineRules(
            { IdRule.validate(accountId) },
            { DiffTransferAccountsRule.validate(fromId, toId) },
        )
}

@LinkedRule(TransferAccountIdRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class TransferAccountIdValidator
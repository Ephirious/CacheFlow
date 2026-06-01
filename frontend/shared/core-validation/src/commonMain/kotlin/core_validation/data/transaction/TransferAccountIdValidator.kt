package core_validation.data.transaction

import core_validation.LinkedRule
import core_validation.ValidationRule
import core_validation.combineRules
import core_validation.data.IdRule
import core_validation.data.transaction.internal.DiffTransferAccountsRule
import core_validation.data.transaction.internal.TransferAccountsContext
import utils.CustomError

object TransferAccountIdRule : ValidationRule<String?, TransferAccountsContext, Nothing?, CustomError> {
    override fun validate(value: String?, ctx: TransferAccountsContext, param: Nothing?): CustomError? {
        return combineRules(
            value, ctx,
            { DiffTransferAccountsRule.validate(value, ctx, param) },
            { IdRule.validate(value, ctx, param) }
        )
    }
}

@LinkedRule(TransferAccountIdRule::class)
@Target(AnnotationTarget.PROPERTY)
annotation class TransferAccountIdValidator
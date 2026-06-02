package transactions.usecases

import core_validation.combineRules
import core_validation.combineStrictRules
import core_validation.data.IdRule
import core_validation.data.transaction.TransactionNoteRule
import core_validation.data.transaction.TransactionValueRule
import core_validation.data.transaction.internal.DiffTransferAccountsRule
import transactions.models.Transaction
import transactions.models.TransactionType
import transactions.repositories.TransactionsRepository

class UpsertTransactionUseCase(
    private val repository: TransactionsRepository,
) {
    suspend operator fun invoke(transaction: Transaction) {
        combineStrictRules(
            { TransactionValueRule.validate(transaction.value.toString()) },
            { TransactionNoteRule.validate(transaction.note) },

            { IdRule.validate(transaction.account.id) },

            {
                when (val t = transaction.type) {
                    is TransactionType.Income -> IdRule.validate(t.category.id)
                    is TransactionType.Outcome -> IdRule.validate(t.category.id)
                    is TransactionType.Transfer ->
                        combineRules(
                            { DiffTransferAccountsRule.validate(fromId = t.from.id, toId = t.to.id) },
                            { IdRule.validate(t.from.id) },
                            { IdRule.validate(t.to.id) },
                        )
                }
            }
        )

        repository.upsertTransaction(transaction.copy(note = transaction.note.trim()))
    }
}
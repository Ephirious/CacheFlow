package manageTransaction.mvi

import editors.models.Account
import editors.models.Category
import manageTransaction.mvi.ManageTransactionType.*
import transactions.models.Transaction
import transactions.models.TransactionType
import utils.types.BigDecimal
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ManageTransactionState.OK.FormState.toDomain(id: String?): Transaction {

    val (accId, catId) = when (val type = transactionType) {
        is Income -> type.accountId to type.categoryId
        is Outcome -> type.accountId to type.categoryId
        is Transfer -> type.fromId to null
    }

    val account = findAccount(accId)
    val category = findCategory(catId)

    if (account == null) throw RuntimeException("Что-то пошло не так")
    if (category == null && !isTransfer) throw RuntimeException("Что-то пошло не так")


    val transaction = Transaction(
        id = id,
        value = BigDecimal(value),
        account = account,
        note = note,
        date = date,
        type = transactionType.toTransaction(this, account, category)
            ?: throw RuntimeException("Что-то пошло не так 67")
    )
    return transaction
}

fun ManageTransactionType.toTransaction(
    form: ManageTransactionState.OK.FormState,
    account: Account,
    category: Category?,
): TransactionType? = when (this) {
    is Income -> TransactionType.Income(category!!)
    is Outcome -> TransactionType.Outcome(category!!)
    is Transfer -> {
        form.findAccount(toId)?.let { targetAcc ->
            TransactionType.Transfer(from = account, to = targetAcc)
        }
    }
}
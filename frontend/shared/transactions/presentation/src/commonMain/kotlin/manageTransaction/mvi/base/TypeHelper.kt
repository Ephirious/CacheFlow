package manageTransaction.mvi.base

import manageTransaction.mvi.ManageTransactionType
import manageTransaction.mvi.ManageTransactionType.*
import manageTransaction.mvi.copyBase


fun ManageTransactionType.updateAccount(newAccountId: String): ManageTransactionType = when (this) {
    is IncomeOrOutcome -> copyBase(accountId = newAccountId)
    is Transfer -> copy(fromId = newAccountId)
}

fun ManageTransactionType.updateCategory(newCategoryId: String): ManageTransactionType = when (this) {
    is IncomeOrOutcome -> copyBase(categoryId = newCategoryId)
    is Transfer -> this
}

fun ManageTransactionType.changeType(newClassName: String): ManageTransactionType {
    val currentAccount = when (this) {
        is IncomeOrOutcome -> accountId; is Transfer -> fromId
    }
    val currentCategory = when (this) {
        is IncomeOrOutcome -> categoryId; is Transfer -> null
    }

    return when (newClassName) {
        "Income" -> Income(currentCategory, currentAccount)
        "Outcome" -> Outcome(currentCategory, currentAccount)
        "Transfer" -> Transfer(currentAccount, null)
        else -> this
    }
}
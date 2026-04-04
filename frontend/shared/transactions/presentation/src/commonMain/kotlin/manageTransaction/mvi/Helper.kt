package manageTransaction.mvi

import manageTransaction.mvi.ManageTransactionType.*


fun ManageTransactionType.updateAccount(newAccountId: String): ManageTransactionType = when (this) {
    is Income -> copy(accountId = newAccountId)
    is Outcome -> copy(accountId = newAccountId)
    is Transfer -> copy(fromId = newAccountId)
}

fun ManageTransactionType.updateCategory(newCategoryId: String): ManageTransactionType = when (this) {
    is Income -> copy(categoryId = newCategoryId)
    is Outcome -> copy(categoryId = newCategoryId)
    is Transfer -> this
}

fun ManageTransactionType.changeType(newClassName: String): ManageTransactionType {
    val currentAccount = when (this) {
        is Income -> accountId; is Outcome -> accountId; is Transfer -> fromId
    }
    val currentCategory = when (this) {
        is Income -> categoryId; is Outcome -> categoryId; is Transfer -> null
    }

    return when (newClassName) {
        "Income" -> Income(currentCategory, currentAccount)
        "Outcome" -> Outcome(currentCategory, currentAccount)
        "Transfer" -> Transfer(currentAccount, null)
        else -> this
    }
}
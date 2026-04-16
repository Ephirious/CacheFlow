package manageTransaction.mvi.base

import dbEnums.CategoryType
import manageTransaction.mvi.ManageTransactionType
import manageTransaction.mvi.ManageTransactionType.*
import manageTransaction.mvi.copyBase
import manageTransaction.mvi.validated
import manageTransaction.mvi.validatedAny


fun ManageTransactionType.updateAccount(newAccountId: String): ManageTransactionType = when (this) {
    is IncomeOrOutcome<*> -> copyBase(accountId = newAccountId)
    is Transfer -> copy(fromId = newAccountId)
}

fun ManageTransactionType.getCategoryType(): CategoryType = when (this) {
    is Income -> CategoryType.INCOME
    else -> CategoryType.OUTCOME
}

fun ManageTransactionType.updateTransferToAccount(newAccountId: String): ManageTransactionType = when (this) {
    is IncomeOrOutcome<*> -> this
    is Transfer -> copy(toId = newAccountId)
}

fun ManageTransactionType.updateCategory(newCategoryId: String): ManageTransactionType = when (this) {
    is IncomeOrOutcome<*> -> copyBase(categoryId = newCategoryId)
    is Transfer -> this
}

fun ManageTransactionType.changeType(newClassName: String): ManageTransactionType {
    val currentAccount = when (this) {
        is IncomeOrOutcome<*> -> accountId; is Transfer -> fromId
    }
    // now currentCategory is always null, cuz income and outcome has different categories now.
//    val currentCategory = when (this) {
//        is IncomeOrOutcome<*> -> categoryId; is Transfer -> null
//    }
    val currentCategory = null

    return when (newClassName) {
        "Income" -> Income(currentCategory, currentAccount)
        "Outcome" -> Outcome(currentCategory, currentAccount)
        "Transfer" -> Transfer(currentAccount, null)
        else -> this
    }
}

fun ManageTransactionType.validated(): ManageTransactionType = when (this) {
    is Income -> this.validated()
    is Outcome -> this.validated()
    is Transfer -> this.validated()
} as ManageTransactionType

fun ManageTransactionType.validated(vararg fields: Any?) = when (this) {
    is Income -> this.validatedAny(*fields) as Income
    is Outcome -> this.validatedAny(*fields) as Outcome
    is Transfer -> this.validatedAny(*fields)
}

fun ManageTransactionType.validationHasErrors() = when (this) {
    is Income -> this.validation.hasErrors
    is Outcome -> this.validation.hasErrors
    is Transfer -> this.validation.hasErrors
}
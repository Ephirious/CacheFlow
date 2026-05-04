package localization

import kotlin.js.JsExport

// -------------------------- MAIN --------------------------
@JsExport
enum class MainSummaryKey: ValidationKey {
    OverallBalance, ProfitPerMonth
}

@JsExport
enum class MainTransactionsKey: ValidationKey {
    Transactions, Filters,
}

@JsExport
enum class ManageTransactionKey: ValidationKey {
    CreateTransaction, EditTransaction,

    // TODO: move to common
    Type, Income, Outcome, Transfer,
    Category, Account, Date, Note
}
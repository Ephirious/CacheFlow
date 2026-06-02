package localization

import kotlin.js.JsExport

// -------------------------- MAIN --------------------------
@JsExport
enum class MainSummaryKey: LocalzKey {
    OverallBalance, ProfitPerMonth
}

@JsExport
enum class MainTransactionsKey: LocalzKey {
    Transactions, Filters,
}

@JsExport
enum class ManageTransactionKey: LocalzKey {
    CreateTransaction, EditTransaction,

    // TODO: move to common
    Type, Income, Outcome, Transfer,
    Category, Account, Date, Note
}
package localization

// -------------------------- MAIN --------------------------
enum class MainSummaryKey: ValidationKey {
    OverallBalance, ProfitPerMonth
}

enum class MainTransactionsKey: ValidationKey {
    Transactions, Filters,
}

enum class ManageTransactionKey: ValidationKey {
    CreateTransaction, EditTransaction,

    // TODO: move to common
    Type, Income, Outcome, Transfer,
    Category, Account, Date, Note
}
package localization

class RuLocalz : Localz {
    override fun by(key: ValidationKey): String = when (key) {
        is MainSummaryKey -> when (key) {
            MainSummaryKey.OverallBalance -> "Общий баланс"
            MainSummaryKey.ProfitPerMonth -> "за месяц"
        }

        is MainTransactionsKey -> when (key) {
            MainTransactionsKey.Transactions -> "Транзакции"
            MainTransactionsKey.Filters -> "Фильтры"
        }

        is ManageTransactionKey -> when (key) {
            ManageTransactionKey.CreateTransaction -> "Новая транзакция"
            ManageTransactionKey.EditTransaction -> "Редактировать"
            ManageTransactionKey.Type -> "Тип"
            ManageTransactionKey.Income -> "Доход"
            ManageTransactionKey.Outcome -> "Расход"
            ManageTransactionKey.Transfer -> "Перевод"
            ManageTransactionKey.Category -> "Категория"
            ManageTransactionKey.Account -> "Счёт"
            ManageTransactionKey.Date -> "Дата"
            ManageTransactionKey.Note -> "Заметка (необязательно)"
        }

        is StringAmountError -> when (key) {
            StringAmountError.EmptyAmount -> "Сумма не может быть пустой"
            StringAmountError.NotANumber -> "Это не очень похоже число"
            StringAmountError.NotPositive -> "Сумма должна быть больше нуля"
            StringAmountError.ScaleExceeded -> "Слишком много знаков после запятой (макс. 2)"
        }

        is LenError -> when (key) {
            is LenError.MaxLengthExceeded -> "Максимальная длина – ${key.limit} символов"
            is LenError.NotExactLength -> "Необходимая длина – ${key.shouldBe} символов"
        }

        NotEmptyOrNullStringError.EmptyOrNullString -> "Не может быть пустым"
        DiffTransferAccountsError.SameAccounts -> "Нельзя перевести на тот же счёт"
        EmailFormatError.InvalidFormat -> "Неверный формат email"
    }


}

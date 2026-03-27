package editors.mappers

import data.Accounts
import editors.models.Account

fun Accounts.toDomain() = Account(
    id = this.id,
    title = this.name,
    balance = this.funds,
    // TODO: Color
)

fun List<Accounts>.listToDomain() = map { it.toDomain() }
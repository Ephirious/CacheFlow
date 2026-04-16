package editors.mappers

import data.Accounts
import editors.models.Account
import utils.types.HexColor

fun Accounts.toDomain() = Account(
    id = this.id,
    title = this.name,
    balance = this.funds,
    color = HexColor(this.color),
)

fun List<Accounts>.listToDomain() = map { it.toDomain() }
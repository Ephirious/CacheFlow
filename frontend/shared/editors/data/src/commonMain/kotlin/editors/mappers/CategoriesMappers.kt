package editors.mappers

import data.Categories
import editors.models.Category

fun Categories.toDomain() = Category(
    id = this.id,
    name = this.name,
    emoji = this.emoji,
    type = this.type,
)

fun List<Categories>.listToDomain() = map { it.toDomain() }
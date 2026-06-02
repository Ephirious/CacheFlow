package editors.usecases.category

import core_validation.combineStrictRules
import core_validation.data.IdRule
import core_validation.data.category.CategoryEmojiRule
import core_validation.data.category.CategoryTitleRule
import editors.repositories.CategoriesRepository


class EditCategoryUseCase(
    private val repository: CategoriesRepository,
) {


    suspend operator fun invoke(id: String, name: String, emoji: String) {

        combineStrictRules(
            { IdRule.validate(id) },
            { CategoryTitleRule.validate(name) },
            { CategoryEmojiRule.validate(emoji) },
        )

        repository.updateCategory(id = id, name = name, emoji = emoji)
    }
}

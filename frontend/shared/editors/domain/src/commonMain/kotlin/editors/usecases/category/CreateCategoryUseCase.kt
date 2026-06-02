package editors.usecases.category

import core_validation.combineStrictRules
import core_validation.data.category.CategoryEmojiRule
import core_validation.data.category.CategoryTitleRule
import dbEnums.CategoryType
import editors.repositories.CategoriesRepository


class CreateCategoryUseCase(
    private val repository: CategoriesRepository,
) {

    suspend operator fun invoke(name: String, emoji: String, type: CategoryType) {
        combineStrictRules(
            { CategoryTitleRule.validate(name) },
            { CategoryEmojiRule.validate(emoji) },
        )

        repository.insertCategory(name = name, emoji = emoji, type = type)
    }
}
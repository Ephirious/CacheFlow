package editors.usecases.category

import dbEnums.CategoryType
import editors.repositories.CategoriesRepository


class CreateCategoryUseCase(
    private val repository: CategoriesRepository,
) {

    suspend operator fun invoke(name: String, emoji: String, type: CategoryType) =
        repository.insertCategory(name = name, emoji = emoji, type = type)
}
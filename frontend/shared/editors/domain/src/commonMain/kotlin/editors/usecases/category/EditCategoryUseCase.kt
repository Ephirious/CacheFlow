package editors.usecases.category

import editors.repositories.CategoriesRepository


class EditCategoryUseCase(
    private val repository: CategoriesRepository,
) {


    suspend operator fun invoke(id: String, name: String, emoji: String) =
        repository.updateCategory(id = id, name = name, emoji = emoji)
}

package editors.usecases.category

import editors.repositories.CategoriesRepository


class GetCategoryByIdUseCase(
    private val repository: CategoriesRepository,
) {
    suspend operator fun invoke(id: String) = repository.getCategoryById(id)
}
package editors.usecases.category

import editors.repositories.CategoriesRepository


class DeleteCategoryUseCase(
    private val repository: CategoriesRepository,
) {


    suspend operator fun invoke(id: String) =
        repository.softDelete(id = id)
}

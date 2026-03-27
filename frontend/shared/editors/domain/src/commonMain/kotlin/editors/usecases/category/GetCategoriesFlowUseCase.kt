package editors.usecases.category

import editors.repositories.CategoriesRepository


class GetCategoriesFlowUseCase(
    private val repository: CategoriesRepository,
) {
    operator fun invoke() = repository.getCategoriesFlow()
}
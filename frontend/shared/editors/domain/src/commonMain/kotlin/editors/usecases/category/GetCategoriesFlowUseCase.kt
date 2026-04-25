package editors.usecases.category

import dbEnums.CategoryType
import editors.models.Category
import editors.repositories.CategoriesRepository
import kotlinx.coroutines.flow.map


data class CategoriesLists(
    val income: List<Category>,
    val outcome: List<Category>
)


class GetCategoriesFlowUseCase(
    private val repository: CategoriesRepository,
) {
    operator fun invoke(onlyActive: Boolean = true) = repository.getCategoriesFlow(onlyActive).map { all ->
        CategoriesLists(
            income = all.filter { it.type == CategoryType.INCOME },
            outcome = all.filter { it.type == CategoryType.OUTCOME }
        )
    }
}
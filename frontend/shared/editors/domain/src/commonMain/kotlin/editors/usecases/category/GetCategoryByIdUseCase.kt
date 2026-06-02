package editors.usecases.category

import core_validation.combineStrictRules
import core_validation.data.IdRule
import editors.models.Category
import editors.repositories.CategoriesRepository


class GetCategoryByIdUseCase(
    private val repository: CategoriesRepository,
) {
    suspend operator fun invoke(id: String): Category {
        combineStrictRules(
            { IdRule.validate(id) }
        )

        return repository.getCategoryById(id)
    }
}
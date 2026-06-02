package editors.usecases.category

import core_validation.combineStrictRules
import core_validation.data.IdRule
import editors.repositories.CategoriesRepository


class DeleteCategoryUseCase(
    private val repository: CategoriesRepository,
) {


    suspend operator fun invoke(id: String) {
        combineStrictRules(
            { IdRule.validate(id) },
        )
        repository.softDelete(id = id)
    }
}

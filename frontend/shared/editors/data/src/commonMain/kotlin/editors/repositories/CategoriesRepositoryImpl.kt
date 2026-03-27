package editors.repositories

import editors.db.CategoriesDatabaseDataSource
import editors.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import utils.presentation.AsyncDispatcher

class CategoriesRepositoryImpl(
    private val databaseDataSource: CategoriesDatabaseDataSource,
) : CategoriesRepository {
    override fun getCategoriesFlow(): Flow<List<Category>> =
        databaseDataSource.getCategoriesFlow().flowOn(AsyncDispatcher)
}
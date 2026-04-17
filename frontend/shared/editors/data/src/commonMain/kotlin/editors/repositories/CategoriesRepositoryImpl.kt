package editors.repositories

import dbEnums.CategoryType
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

    override suspend fun getCategoryById(id: String): Category =
        databaseDataSource.getCategoryById(id)

    override suspend fun insertCategory(name: String, emoji: String, type: CategoryType) =
        databaseDataSource.insertCategory(name = name, emoji = emoji, type = type)

    override suspend fun updateCategory(id: String, name: String, emoji: String) =
        databaseDataSource.updateCategory(id = id, name = name, emoji = emoji)
}
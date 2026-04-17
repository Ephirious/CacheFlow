package editors.db

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.CategoriesQueries
import editors.mappers.listToDomain
import editors.mappers.toDomain
import editors.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import utils.presentation.AsyncDispatcher

class CategoriesDatabaseDataSource(
    private val categoriesQueries: CategoriesQueries
) {
    fun getCategoriesFlow(): Flow<List<Category>> {
        return categoriesQueries.selectAll()
            .asFlow()
            .mapToList(AsyncDispatcher)
            .map { entity ->
                entity.listToDomain()
            }
    }

    suspend fun getCategoryById(id: String): Category = categoriesQueries.selectById(id).awaitAsOne().toDomain()
}
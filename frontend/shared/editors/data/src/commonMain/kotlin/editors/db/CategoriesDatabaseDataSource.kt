package editors.db

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.CategoriesQueries
import dbEnums.CategoryType
import editors.mappers.listToDomain
import editors.mappers.toDomain
import editors.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import utils.presentation.AsyncDispatcher
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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


    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertCategory(
        name: String,
        emoji: String,
        type: CategoryType,
    ) {
        val id = Uuid.generateV7().toString()
        categoriesQueries.insert(id = id, name = name, emoji = emoji, type = type)
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateCategory(
        id: String,
        name: String,
        emoji: String,
    ) {
        categoriesQueries.update(id = id, name = name, emoji = emoji)
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun softDeleteCategory(id: String) = categoriesQueries.softDelete(id)

    @OptIn(ExperimentalUuidApi::class)
    suspend fun upsertCategory(
        id: String,
        name: String,
        emoji: String,
        type: CategoryType,
    ) {
        categoriesQueries.upsert(id = id, name = name, emoji = emoji, type = type)
    }

}
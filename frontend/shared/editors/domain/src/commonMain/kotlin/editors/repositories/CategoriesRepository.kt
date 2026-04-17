package editors.repositories

import dbEnums.CategoryType
import editors.models.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategoriesFlow(): Flow<List<Category>>

    suspend fun getCategoryById(id: String): Category

    suspend fun insertCategory(
        name: String,
        emoji: String,
        type: CategoryType,
    )

    suspend fun updateCategory(
        id: String,
        name: String,
        emoji: String,
    )
}
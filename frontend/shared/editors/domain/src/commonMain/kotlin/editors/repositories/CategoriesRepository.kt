package editors.repositories

import editors.models.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategoriesFlow(): Flow<List<Category>>

    suspend fun getCategoryById(id: String): Category
}
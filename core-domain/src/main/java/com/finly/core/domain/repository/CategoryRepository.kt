package com.finly.core.domain.repository

import com.finly.core.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: String): Category?
    suspend fun insertCategory(category: Category)
    suspend fun initDefaultCategories()
}

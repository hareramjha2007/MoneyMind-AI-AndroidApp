package com.finly.core.data.repository

import com.finly.core.data.dao.CategoryDao
import com.finly.core.data.entity.CategoryEntity
import com.finly.core.domain.model.Category
import com.finly.core.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return dao.getAllCategories().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getCategoryById(id: String): Category? {
        return dao.getCategoryById(id)?.toDomain()
    }

    override suspend fun insertCategory(category: Category) {
        dao.insertCategory(CategoryEntity.fromDomain(category))
    }

    override suspend fun initDefaultCategories() {
        val defaultEntities = Category.SYSTEM_CATEGORIES.map { CategoryEntity.fromDomain(it) }
        dao.insertCategories(defaultEntities)
    }
}

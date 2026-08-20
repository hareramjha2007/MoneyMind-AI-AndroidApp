package com.finly.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finly.core.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isSystemDefault: Boolean,
    val parentCategoryId: String?
) {
    fun toDomain(): Category {
        return Category(
            id = id,
            name = name,
            iconName = iconName,
            colorHex = colorHex,
            isSystemDefault = isSystemDefault,
            parentCategoryId = parentCategoryId
        )
    }

    companion object {
        fun fromDomain(domain: Category): CategoryEntity {
            return CategoryEntity(
                id = domain.id,
                name = domain.name,
                iconName = domain.iconName,
                colorHex = domain.colorHex,
                isSystemDefault = domain.isSystemDefault,
                parentCategoryId = domain.parentCategoryId
            )
        }
    }
}

package com.finly.core.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TransactionCategory(
    val categoryId: String,
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    BILLS("bills", "Bills", Icons.Rounded.ReceiptLong, Color(0xFF10B981)),
    EMI("emi", "EMI", Icons.Rounded.AccountBalance, Color(0xFF94A3B8)),
    SHOPPING("shopping", "Shopping", Icons.Rounded.ShoppingCart, Color(0xFF06B6D4)),
    INVESTMENT("investment", "Investment", Icons.Rounded.TrendingUp, Color(0xFF3B82F6)),
    MEDICAL("medical", "Dr & Medicine", Icons.Rounded.MedicalServices, Color(0xFF84CC16)),
    FUEL("fuel", "Fuel", Icons.Rounded.LocalGasStation, Color(0xFFF59E0B)),
    FOOD("food", "Food & Drinks", Icons.Rounded.Restaurant, Color(0xFFEC4899)),
    LEARNING("learning", "Learning", Icons.Rounded.School, Color(0xFF0EA5E9)),
    TRANSFER("transfer", "Transfer", Icons.Rounded.SwapHoriz, Color(0xFFA855F7)),
    UNKNOWN("unknown", "Unknown", Icons.Rounded.Category, Color(0xFFEF4444));

    companion object {
        fun fromId(id: String): TransactionCategory {
            val cleanId = id.trim().lowercase()
            return values().firstOrNull { 
                cleanId.contains(it.categoryId) || it.categoryId.contains(cleanId) 
            } ?: UNKNOWN
        }
    }
}

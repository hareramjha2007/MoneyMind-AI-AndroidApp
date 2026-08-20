package com.finly.core.domain.model

data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isSystemDefault: Boolean = true,
    val parentCategoryId: String? = null
) {
    companion object {
        val SYSTEM_CATEGORIES = listOf(
            Category("food", "Food & Dining", "restaurant", "#FF6B6B"),
            Category("groceries", "Groceries", "shopping_cart", "#4ECDC4"),
            Category("transport", "Transport & Fuel", "directions_car", "#45B7D1"),
            Category("shopping", "Shopping", "shopping_bag", "#96CEB4"),
            Category("bills", "Bills & Utilities", "receipt_long", "#FFEEAD"),
            Category("subscriptions", "Subscriptions", "subscriptions", "#D4A5A5"),
            Category("health", "Health & Wellness", "health_and_safety", "#9B59B6"),
            Category("entertainment", "Entertainment", "movie", "#E67E22"),
            Category("transfer", "Transfers & Self", "swap_horiz", "#34495E"),
            Category("income", "Salary & Income", "account_balance_wallet", "#2ECC71"),
            Category("others", "Miscellaneous", "more_horiz", "#95A5A6")
        )
    }
}

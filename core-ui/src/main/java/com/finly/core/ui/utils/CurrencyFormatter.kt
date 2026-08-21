package com.finly.core.ui.utils

import java.text.NumberFormat
import java.util.Locale

data class CurrencyOption(
    val code: String,
    val name: String,
    val symbol: String,
    val locale: Locale
)

object CurrencyFormatter {

    val supportedCurrencies = listOf(
        CurrencyOption("INR", "Indian Rupee (₹)", "₹", Locale("en", "IN")),
        CurrencyOption("USD", "US Dollar ($)", "$", Locale.US),
        CurrencyOption("EUR", "Euro (€)", "€", Locale.GERMANY),
        CurrencyOption("GBP", "British Pound (£)", "£", Locale.UK),
        CurrencyOption("AED", "UAE Dirham (AED)", "AED ", Locale("ar", "AE")),
        CurrencyOption("SGD", "Singapore Dollar (S$)", "S$", Locale("en", "SG")),
        CurrencyOption("CAD", "Canadian Dollar (CA$)", "CA$", Locale.CANADA),
        CurrencyOption("AUD", "Australian Dollar (A$)", "A$", Locale("en", "AU"))
    )

    var currentCurrencyCode: String = "INR"

    fun getOption(code: String = currentCurrencyCode): CurrencyOption {
        return supportedCurrencies.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: supportedCurrencies[0]
    }

    /**
     * Formats any numeric double into the user's active currency format (e.g. ₹17,672.00, $17,672.00, €17,672.00)
     */
    fun formatInr(amount: Double, showSymbol: Boolean = true): String {
        val option = getOption(currentCurrencyCode)
        return try {
            val formatter = NumberFormat.getCurrencyInstance(option.locale).apply {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
            }
            val formatted = formatter.format(amount)
            if (!showSymbol) {
                formatted.replace(option.symbol, "").trim()
            } else {
                formatted
            }
        } catch (e: Exception) {
            val rounded = String.format(option.locale, "%.2f", amount)
            if (showSymbol) "${option.symbol}$rounded" else rounded
        }
    }
}

package com.finly.core.data.parser.engine

import java.util.Locale

data class CategoryResult(
    val category: String,
    val categoryConfidence: Float
)

object CategoryEngine {

    fun categorize(merchant: String?, fullText: String, isCredit: Boolean = false): CategoryResult {
        val searchSpace = "${merchant?.lowercase(Locale.ROOT) ?: ""} ${fullText.lowercase(Locale.ROOT)}"

        if (isCredit) {
            return when {
                listOf("salary", "payroll", "stipend", "remuneration").any { searchSpace.contains(it) } -> CategoryResult("Salary", 0.95f)
                listOf("refund", "cashback", "reversal").any { searchSpace.contains(it) } -> CategoryResult("Income", 0.90f)
                listOf("dividend", "interest", "zerodha", "groww", "upstox").any { searchSpace.contains(it) } -> CategoryResult("Investment", 0.90f)
                else -> CategoryResult("Income", 0.75f)
            }
        }

        return when {
            // Food & Dining
            listOf("swiggy", "zomato", "mcdonald", "starbucks", "domino", "pizza", "kfc", "restaurant", "cafe", "dining", "eatery", "bakery").any { searchSpace.contains(it) } -> CategoryResult("Food & Dining", 0.95f)

            // Groceries
            listOf("instamart", "zepto", "blinkit", "bigbasket", "dmart", "grocery", "supermarket", "reliance fresh").any { searchSpace.contains(it) } -> CategoryResult("Groceries", 0.95f)

            // Fuel
            listOf("shell", "bpcl", "hpcl", "iocl", "fuel", "petrol", "diesel", "gas station").any { searchSpace.contains(it) } -> CategoryResult("Fuel", 0.95f)

            // Transport & Travel
            listOf("uber", "ola", "rapido", "metro", "irctc", "redbus", "indigo", "air india", "vistara", "flight", "cleartrip", "makemytrip").any { searchSpace.contains(it) } -> CategoryResult("Transport", 0.90f)

            // Shopping
            listOf("amazon", "amzn", "flipkart", "myntra", "ajio", "nykaa", "zudio", "zara", "uniqlo", "shopping", "store", "apparel").any { searchSpace.contains(it) } -> CategoryResult("Shopping", 0.90f)

            // Subscriptions
            listOf("netflix", "spotify", "prime", "youtube", "hotstar", "jiocinema", "subscription", "recurring").any { searchSpace.contains(it) } -> CategoryResult("Subscriptions", 0.95f)

            // Utilities
            listOf("bescom", "electricity", "airtel", "jio", "vi", "bsnl", "bill", "recharge", "utility", "mobile", "broadband", "water bill").any { searchSpace.contains(it) } -> CategoryResult("Utilities", 0.90f)

            // Healthcare
            listOf("apollo", "medplus", "pharmeasy", "1mg", "hospital", "clinic", "doctor", "pharmacy", "medical", "lab test").any { searchSpace.contains(it) } -> CategoryResult("Healthcare", 0.95f)

            // EMI & Loans
            listOf("emi", "loan", "bajaj finance", "hdfc bank loan", "icici home loan", "equated monthly").any { searchSpace.contains(it) } -> CategoryResult("EMI & Loans", 0.95f)

            // Insurance
            listOf("lic", "hdfc life", "icici prudential", "max life", "insurance", "premium").any { searchSpace.contains(it) } -> CategoryResult("Insurance", 0.90f)

            // Investment
            listOf("zerodha", "groww", "upstox", "mutual fund", "sip", "coin", "indmoney", "smallcase", "gold", "nse", "bse").any { searchSpace.contains(it) } -> CategoryResult("Investment", 0.95f)

            // Business & Professional
            listOf("vyapar", "zoho", "tally", "aws", "google cloud", "domain", "hosting", "invoice").any { searchSpace.contains(it) } -> CategoryResult("Business Expense", 0.90f)

            // ATM & Cash
            listOf("atm", "cash withdrawal", "withdrawn at atm").any { searchSpace.contains(it) } -> CategoryResult("ATM Withdrawal", 0.95f)

            // Transfers
            listOf("transfer to", "sent to friend", "self transfer", "wallet topup").any { searchSpace.contains(it) } -> CategoryResult("Transfer", 0.85f)

            else -> CategoryResult("Other", 0.50f)
        }
    }
}

package com.finly.core.data.parser

import com.finly.core.domain.model.TransactionDirection
import java.util.Locale
import java.util.regex.Pattern

class TransactionParserEngine {

    private val otpKeywords = listOf(
        "otp", "verification code", "one time password", "secret code",
        "do not share", "pre-approved", "apply for", "loan", "reward points"
    )

    private val debitKeywords = listOf(
        "debited", "paid", "spent", "sent", "transferred to", "withdrawn", "purchase at", "dr"
    )

    private val creditKeywords = listOf(
        "credited", "received", "deposited", "refund", "cashback", "cr"
    )

    // Regex for amount: e.g. Rs. 1,250.00, Rs1250, INR 450.50, Rs.500
    private val amountPattern = Pattern.compile(
        "(?:Rs\\.?|INR|₹)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    // Common merchant extractors
    private val merchantAtPattern = Pattern.compile("at\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+on|\\s+ref|\\s+via|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)
    private val merchantToPattern = Pattern.compile("to\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+on|\\s+ref|\\s+via|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)

    fun parseMessage(text: String, senderId: String, packageName: String = "sms"): ParseResult {
        val lowerText = text.lowercase(Locale.ROOT)

        // 1. Explicit OTP & Non-transactional filter
        if (otpKeywords.any { lowerText.contains(it) }) {
            return ParseResult.IgnoredNonTransactional
        }

        // 2. Extract Amount
        val matcher = amountPattern.matcher(text)
        if (!matcher.find()) {
            return ParseResult.FailedToParse
        }

        val amountStr = matcher.group(1)?.replace(",", "") ?: return ParseResult.FailedToParse
        val amount = amountStr.toDoubleOrNull() ?: return ParseResult.FailedToParse

        if (amount <= 0.0) return ParseResult.FailedToParse

        // 3. Determine Direction
        val isDebit = debitKeywords.any { lowerText.contains(it) }
        val isCredit = creditKeywords.any { lowerText.contains(it) }

        if (!isDebit && !isCredit) {
            return ParseResult.FailedToParse
        }

        val direction = if (isDebit) TransactionDirection.DEBIT else TransactionDirection.CREDIT

        // 4. Extract Merchant
        var merchant: String? = null
        val atMatcher = merchantAtPattern.matcher(text)
        if (atMatcher.find()) {
            merchant = atMatcher.group(1)?.trim()
        } else {
            val toMatcher = merchantToPattern.matcher(text)
            if (toMatcher.find()) {
                merchant = toMatcher.group(1)?.trim()
            }
        }

        // Clean merchant string
        merchant = merchant?.take(30)

        // 5. Deduce Category
        val categoryId = deduceCategory(merchant, lowerText)

        return ParseResult.Success(
            transaction = ParsedTransaction(
                amount = amount,
                direction = direction,
                merchant = merchant,
                categoryId = categoryId,
                rawSenderId = senderId,
                sourceApp = packageName,
                confidenceScore = if (merchant != null) 0.95f else 0.80f
            )
        )
    }

    private fun deduceCategory(merchant: String?, fullTextLower: String): String {
        val searchSpace = "${merchant?.lowercase(Locale.ROOT) ?: ""} $fullTextLower"

        return when {
            listOf("swiggy", "zomato", "mcdonald", "starbucks", "domino", "kfc", "restaurant", "cafe", "dining").any { searchSpace.contains(it) } -> "food"
            listOf("instamart", "zepto", "blinkit", "bigbasket", "dmart", "grocery", "supermarket").any { searchSpace.contains(it) } -> "groceries"
            listOf("uber", "ola", "rapido", "shell", "bpcl", "hpcl", "fuel", "petrol", "metro").any { searchSpace.contains(it) } -> "transport"
            listOf("amazon", "flipkart", "myntra", "zudio", "zara", "uniqlo", "shopping").any { searchSpace.contains(it) } -> "shopping"
            listOf("netflix", "spotify", "prime", "youtube", "hotstar", "subscription").any { searchSpace.contains(it) } -> "subscriptions"
            listOf("bescom", "electricity", "airtel", "jio", "vi", "bill", "recharge", "utility").any { searchSpace.contains(it) } -> "bills"
            listOf("salary", "payroll", "stipend").any { searchSpace.contains(it) } -> "income"
            else -> "others"
        }
    }
}

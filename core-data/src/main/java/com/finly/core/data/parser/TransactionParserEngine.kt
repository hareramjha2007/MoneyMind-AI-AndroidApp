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
        "debited", "paid", "spent", "sent", "transferred to", "withdrawn", "purchase at", "dr", "at "
    )

    private val creditKeywords = listOf(
        "credited", "credited with", "received", "deposited", "refund", "cashback", "cr"
    )

    // Universal Regex for Amount: Matches ₹, Rs., Rs, INR followed by monetary figures
    private val amountPattern = Pattern.compile(
        "(?:Rs\\.?|INR|₹)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    // Merchant extraction patterns (prefer "at <merchant>" first, then "for <merchant>" / "to <merchant>")
    private val merchantAtPattern = Pattern.compile("at\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+using|\\s+for|\\s+on|\\s+ref|\\s+via|\\s+total|\\s+your|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)
    private val merchantToPattern = Pattern.compile("(?:to|for\\s+purchase\\s+at|for)\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+using|\\s+for|\\s+on|\\s+ref|\\s+via|\\s+total|\\s+your|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)

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

        // 3. Determine Direction (Safely ignoring "credit card" or "ICICI credit (xxxx)" instrument names)
        val textWithoutInstrument = lowerText
            .replace(Regex("credit\\s+card\\s*\\([0-9]+\\)"), "")
            .replace(Regex("credit\\s*\\([0-9]+\\)"), "")
            .replace("credit card", "")

        var isDebit = debitKeywords.any { textWithoutInstrument.contains(it) }
        val isCredit = creditKeywords.any { textWithoutInstrument.contains(it) }

        // 🛡️ UNIVERSAL FAIL-SAFE RULE: If a monetary notification comes from a bank/tracker app,
        // and is not explicitly a credit deposit, DEFAULT TO DEBIT (Expense).
        // This guarantees that ANY future or unknown notification format is NEVER lost!
        if (!isDebit && !isCredit) {
            isDebit = true
        }

        val direction = if (isCredit && !isDebit) TransactionDirection.CREDIT else TransactionDirection.DEBIT

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
        merchant = merchant
            ?.replace(Regex("(?i)\\s+(purchase|using|total|your|spent|visit).*"), "")
            ?.trim()
            ?.take(30)

        if (merchant.isNull_blank()) {
            merchant = null
        }

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
                confidenceScore = if (merchant != null && (isDebit || isCredit)) 0.95f else 0.75f
            )
        )
    }

    private fun String?.isNull_blank(): Boolean = this == null || this.isBlank()

    private fun deduceCategory(merchant: String?, fullTextLower: String): String {
        val searchSpace = "${merchant?.lowercase(Locale.ROOT) ?: ""} $fullTextLower"

        return when {
            listOf("swiggy", "zomato", "mcdonald", "starbucks", "domino", "kfc", "restaurant", "cafe", "dining").any { searchSpace.contains(it) } -> "food"
            listOf("instamart", "zepto", "blinkit", "bigbasket", "dmart", "grocery", "supermarket").any { searchSpace.contains(it) } -> "groceries"
            listOf("uber", "ola", "rapido", "shell", "bpcl", "hpcl", "fuel", "petrol", "metro").any { searchSpace.contains(it) } -> "transport"
            listOf("amazon", "flipkart", "myntra", "zudio", "zara", "uniqlo", "shopping").any { searchSpace.contains(it) } -> "shopping"
            listOf("netflix", "spotify", "prime", "youtube", "hotstar", "subscription").any { searchSpace.contains(it) } -> "subscriptions"
            listOf("bescom", "electricity", "airtel", "jio", "vi", "bsnl", "bill", "recharge", "utility", "mobile").any { searchSpace.contains(it) } -> "bills"
            listOf("salary", "payroll", "stipend").any { searchSpace.contains(it) } -> "income"
            else -> "others"
        }
    }
}

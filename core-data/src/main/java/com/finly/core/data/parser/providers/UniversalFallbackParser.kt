package com.finly.core.data.parser.providers

import java.util.Locale
import java.util.regex.Pattern

class UniversalFallbackParser : BaseProviderParser() {

    override val providerId: String = "Bank"

    private val merchantAtPattern = Pattern.compile("at\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+using|\\s+for|\\s+on|\\s+ref|\\s+via|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)
    private val merchantToPattern = Pattern.compile("(?:to|for)\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+using|\\s+for|\\s+on|\\s+ref|\\s+via|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean = true

    override fun parse(packageName: String, senderId: String, text: String, postTime: Long): ParsedTransactionResult? {
        val amount = extractAmount(text) ?: return null
        val isCredit = isCreditTransaction(text)
        
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

        return buildParsedResult(
            amount = amount,
            isCredit = isCredit,
            rawMerchant = merchant,
            providerName = "Bank",
            packageName = packageName,
            text = text,
            postTime = postTime
        )
    }
}

package com.finly.core.data.parser.providers

import java.util.Locale
import java.util.regex.Pattern

class HdfcParser : BaseProviderParser() {

    override val providerId: String = "HDFC"

    private val merchantAtPattern = Pattern.compile("at\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+on|\\s+using|\\s+ref|\\s+via|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)
    private val merchantToPattern = Pattern.compile("(?:to|vpa)\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+on|\\s+using|\\s+ref|\\s+via|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean {
        val search = "$packageName $senderId $text".lowercase(Locale.ROOT)
        return search.contains("hdfc") || search.contains("hdfcbk")
    }

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
            providerName = "HDFC",
            packageName = packageName,
            text = text,
            postTime = postTime
        )
    }
}

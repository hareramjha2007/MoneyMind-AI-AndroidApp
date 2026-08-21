package com.finly.core.data.parser.providers

import java.util.Locale
import java.util.regex.Pattern

class AxioParser : BaseProviderParser() {

    override val providerId: String = "Axio"

    private val merchantAtPattern = Pattern.compile("at\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+total|\\s+using|\\s+for|\\s+spent|\\.|\\,|$|\\₹|rs|inr)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean {
        val search = "$packageName $senderId $text".lowercase(Locale.ROOT)
        return search.contains("axio") || search.contains("walnut") || packageName == "com.daamitt.walnut.app"
    }

    override fun parse(packageName: String, senderId: String, text: String, postTime: Long): ParsedTransactionResult? {
        val amount = extractAmount(text) ?: return null
        val isCredit = isCreditTransaction(text)

        var merchant: String? = null
        val matcher = merchantAtPattern.matcher(text)
        if (matcher.find()) {
            merchant = matcher.group(1)?.trim()
        }

        return buildParsedResult(
            amount = amount,
            isCredit = isCredit,
            rawMerchant = merchant,
            providerName = "Axio",
            packageName = packageName,
            text = text,
            postTime = postTime
        )
    }
}

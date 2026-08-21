package com.finly.core.data.parser.providers

import java.util.Locale
import java.util.regex.Pattern

class IciciParser : BaseProviderParser() {

    override val providerId: String = "ICICI"

    private val merchantAtPattern = Pattern.compile("at\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+on|\\s+using|\\s+ref|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean {
        val search = "$packageName $senderId $text".lowercase(Locale.ROOT)
        return search.contains("icici") || search.contains("icicib")
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
            providerName = "ICICI",
            packageName = packageName,
            text = text,
            postTime = postTime
        )
    }
}

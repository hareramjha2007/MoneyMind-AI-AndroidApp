package com.finly.core.data.parser.providers

import java.util.Locale
import java.util.regex.Pattern

class SbiParser : BaseProviderParser() {

    override val providerId: String = "SBI"

    private val merchantToPattern = Pattern.compile("(?:to|trfr to|paid to)\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+ref|\\s+via|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean {
        val search = "$packageName $senderId $text".lowercase(Locale.ROOT)
        return search.contains("sbi") || search.contains("sbin") || search.contains("state bank")
    }

    override fun parse(packageName: String, senderId: String, text: String, postTime: Long): ParsedTransactionResult? {
        val amount = extractAmount(text) ?: return null
        val isCredit = isCreditTransaction(text)

        var merchant: String? = null
        val matcher = merchantToPattern.matcher(text)
        if (matcher.find()) {
            merchant = matcher.group(1)?.trim()
        }

        return buildParsedResult(
            amount = amount,
            isCredit = isCredit,
            rawMerchant = merchant,
            providerName = "SBI",
            packageName = packageName,
            text = text,
            postTime = postTime
        )
    }
}

package com.finly.core.data.parser.providers

import java.util.Locale
import java.util.regex.Pattern

class KotakParser : BaseProviderParser() {

    override val providerId: String = "Kotak"

    private val merchantPattern = Pattern.compile("(?:at|to)\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\s+on|\\s+ref|\\.|\\,|$)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean {
        val search = "$packageName $senderId $text".lowercase(Locale.ROOT)
        return search.contains("kotak") || search.contains("kbank")
    }

    override fun parse(packageName: String, senderId: String, text: String, postTime: Long): ParsedTransactionResult? {
        val amount = extractAmount(text) ?: return null
        val isCredit = isCreditTransaction(text)

        var merchant: String? = null
        val matcher = merchantPattern.matcher(text)
        if (matcher.find()) {
            merchant = matcher.group(1)?.trim()
        }

        return buildParsedResult(
            amount = amount,
            isCredit = isCredit,
            rawMerchant = merchant,
            providerName = "Kotak",
            packageName = packageName,
            text = text,
            postTime = postTime
        )
    }
}

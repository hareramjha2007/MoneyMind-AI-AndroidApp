package com.finly.core.data.parser.providers

import java.util.Locale
import java.util.regex.Pattern

class CredParser : BaseProviderParser() {

    override val providerId: String = "CRED"

    private val merchantPattern = Pattern.compile("(?:at|to|paid)\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\.|\\,|$|\\s+using)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean {
        val cleanPkg = packageName.lowercase(Locale.ROOT)
        val cleanSender = senderId.lowercase(Locale.ROOT)
        val cleanText = text.lowercase(Locale.ROOT)

        return cleanPkg == "com.dreamplug.androidapp" ||
                cleanPkg.contains("cred") ||
                cleanSender.contains("cred") ||
                cleanText.contains("cred pay") ||
                cleanText.contains("cred cash") ||
                cleanText.contains("via cred")
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
            providerName = "CRED",
            packageName = packageName,
            text = text,
            postTime = postTime
        )
    }
}

package com.finly.core.data.parser.providers

import com.finly.core.domain.model.TransactionType
import java.util.Locale
import java.util.regex.Pattern

class PaytmParser : BaseProviderParser() {

    override val providerId: String = "Paytm"

    private val merchantPattern = Pattern.compile("(?:at|to|paid to)\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\.|\\,|$|\\s+using)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean {
        val search = "$packageName $senderId $text".lowercase(Locale.ROOT)
        return search.contains("paytm") || packageName == "net.one97.paytm"
    }

    override fun parse(packageName: String, senderId: String, text: String, postTime: Long): ParsedTransactionResult? {
        val amount = extractAmount(text) ?: return null
        val lowerText = text.lowercase(Locale.ROOT)
        val isCredit = lowerText.contains("received") || lowerText.contains("added") || lowerText.contains("credited")

        var merchant: String? = null
        val matcher = merchantPattern.matcher(text)
        if (matcher.find()) {
            merchant = matcher.group(1)?.trim()
        }

        return buildParsedResult(
            amount = amount,
            isCredit = isCredit,
            rawMerchant = merchant,
            providerName = "Paytm",
            packageName = packageName,
            text = text,
            postTime = postTime,
            customType = TransactionType.UPI
        )
    }
}

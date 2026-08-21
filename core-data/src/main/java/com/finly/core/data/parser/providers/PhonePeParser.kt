package com.finly.core.data.parser.providers

import com.finly.core.domain.model.TransactionType
import java.util.Locale
import java.util.regex.Pattern

class PhonePeParser : BaseProviderParser() {

    override val providerId: String = "PhonePe"

    private val merchantPaidToPattern = Pattern.compile("paid\\s+to\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\.|\\,|$|\\s+using)", Pattern.CASE_INSENSITIVE)
    private val merchantReceivedFromPattern = Pattern.compile("received\\s+from\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\.|\\,|$|\\s+in)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean {
        val search = "$packageName $senderId $text".lowercase(Locale.ROOT)
        return search.contains("phonepe") || packageName == "com.phonepe.app"
    }

    override fun parse(packageName: String, senderId: String, text: String, postTime: Long): ParsedTransactionResult? {
        val amount = extractAmount(text) ?: return null
        val lowerText = text.lowercase(Locale.ROOT)
        val isCredit = lowerText.contains("received from") || lowerText.contains("credited")

        var merchant: String? = null
        if (isCredit) {
            val matcher = merchantReceivedFromPattern.matcher(text)
            if (matcher.find()) {
                merchant = matcher.group(1)?.trim()
            }
        } else {
            val matcher = merchantPaidToPattern.matcher(text)
            if (matcher.find()) {
                merchant = matcher.group(1)?.trim()
            }
        }

        return buildParsedResult(
            amount = amount,
            isCredit = isCredit,
            rawMerchant = merchant,
            providerName = "PhonePe",
            packageName = packageName,
            text = text,
            postTime = postTime,
            customType = TransactionType.UPI
        )
    }
}

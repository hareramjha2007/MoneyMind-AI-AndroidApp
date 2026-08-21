package com.finly.core.data.parser.providers

import com.finly.core.domain.model.TransactionType
import java.util.Locale
import java.util.regex.Pattern

class GooglePayParser : BaseProviderParser() {

    override val providerId: String = "Google Pay"

    private val merchantPaidToPattern = Pattern.compile("(?:paid|sent)\\s+([A-Za-z0-9\\s\\.\\&\\-]+?)(?:\\.|\\,|$|\\s+using)", Pattern.CASE_INSENSITIVE)

    override fun canParse(packageName: String, senderId: String, text: String): Boolean {
        val search = "$packageName $senderId $text".lowercase(Locale.ROOT)
        return search.contains("gpay") || search.contains("google pay") || packageName == "com.google.android.apps.nbu.paisa.user"
    }

    override fun parse(packageName: String, senderId: String, text: String, postTime: Long): ParsedTransactionResult? {
        val amount = extractAmount(text) ?: return null
        val lowerText = text.lowercase(Locale.ROOT)
        val isCredit = lowerText.contains("received") || lowerText.contains("credited")

        var merchant: String? = null
        val matcher = merchantPaidToPattern.matcher(text)
        if (matcher.find()) {
            merchant = matcher.group(1)?.trim()
        }

        return buildParsedResult(
            amount = amount,
            isCredit = isCredit,
            rawMerchant = merchant,
            providerName = "Google Pay",
            packageName = packageName,
            text = text,
            postTime = postTime,
            customType = TransactionType.UPI
        )
    }
}

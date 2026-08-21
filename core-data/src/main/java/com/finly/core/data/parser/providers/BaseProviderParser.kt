package com.finly.core.data.parser.providers

import com.finly.core.data.parser.engine.AccountResolver
import com.finly.core.data.parser.engine.CategoryEngine
import com.finly.core.data.parser.engine.ConfidenceEngine
import com.finly.core.data.parser.engine.MerchantResolver
import com.finly.core.data.parser.engine.SourceResolver
import com.finly.core.domain.model.TransactionDirection
import com.finly.core.domain.model.TransactionType
import java.util.Locale
import java.util.regex.Pattern

abstract class BaseProviderParser : ProviderParser {

    protected val amountPattern = Pattern.compile(
        "(?:Rs\\.?|INR|₹)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    protected val refNoPattern = Pattern.compile(
        "(?:ref(?:no|num)?|upi\\s*ref|rrn|txn\\s*id)\\s*[:\\s]*([0-9A-Za-z]{6,18})",
        Pattern.CASE_INSENSITIVE
    )

    protected val upiIdPattern = Pattern.compile(
        "([a-zA-Z0-9\\.\\-_]+@[a-zA-Z0-9]+)",
        Pattern.CASE_INSENSITIVE
    )

    protected fun extractAmount(text: String): Double? {
        val matcher = amountPattern.matcher(text)
        if (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "") ?: return null
            return amountStr.toDoubleOrNull()
        }
        return null
    }

    protected fun extractRefNo(text: String): String? {
        val matcher = refNoPattern.matcher(text)
        return if (matcher.find()) matcher.group(1) else null
    }

    protected fun extractUpiId(text: String): String? {
        val matcher = upiIdPattern.matcher(text)
        return if (matcher.find()) matcher.group(1) else null
    }

    protected fun isCreditTransaction(text: String): Boolean {
        val cleanText = text.lowercase(Locale.ROOT)
            .replace(Regex("credit\\s+card\\s*\\([0-9]+\\)"), "")
            .replace(Regex("credit\\s*\\([0-9]+\\)"), "")
            .replace(Regex("credit\\s+[0-9]+"), "")
            .replace("credit card", "")

        val isCreditKeyword = cleanText.contains("credited") ||
                cleanText.contains("credited by") ||
                cleanText.contains("received from") ||
                cleanText.contains("cashback") ||
                cleanText.contains("cr.") ||
                cleanText.contains("deposit")

        val isDebitKeyword = cleanText.contains("debited") ||
                cleanText.contains("spent") ||
                cleanText.contains("paid to") ||
                cleanText.contains("transferred to") ||
                cleanText.contains("withdrawn")

        return isCreditKeyword && !isDebitKeyword
    }

    protected fun buildParsedResult(
        amount: Double,
        isCredit: Boolean,
        rawMerchant: String?,
        providerName: String,
        packageName: String,
        text: String,
        postTime: Long,
        customType: TransactionType? = null
    ): ParsedTransactionResult {
        val direction = if (isCredit) TransactionDirection.CREDIT else TransactionDirection.DEBIT
        val transactionType = customType ?: if (isCredit) TransactionType.CREDIT else TransactionType.DEBIT

        val resolvedMerchant = MerchantResolver.resolveMerchant(rawMerchant, text)
        val categoryResult = CategoryEngine.categorize(resolvedMerchant.merchantNormalized, text, isCredit)
        val sourceInfo = SourceResolver.resolveSource(packageName, text)
        val accountLast4 = AccountResolver.resolveAccountLast4(text)
        val refNo = extractRefNo(text)
        val upiId = extractUpiId(text)

        val confidence = ConfidenceEngine.calculateConfidence(
            hasMerchant = resolvedMerchant.merchantNormalized != null,
            categoryConfidence = categoryResult.categoryConfidence,
            hasProvider = providerName != "Bank",
            hasAccountLast4 = accountLast4 != null,
            isCredit = isCredit
        )

        return ParsedTransactionResult(
            amount = amount,
            direction = direction,
            transactionType = transactionType,
            merchantRaw = resolvedMerchant.merchantRaw,
            merchantNormalized = resolvedMerchant.merchantNormalized,
            category = categoryResult.category,
            providerName = providerName,
            sourceApp = sourceInfo.sourceApp,
            accountLast4 = accountLast4,
            upiId = upiId,
            referenceNumber = refNo,
            timestamp = if (postTime > 0) postTime else System.currentTimeMillis(),
            confidenceScore = confidence,
            rawNotification = text
        )
    }
}

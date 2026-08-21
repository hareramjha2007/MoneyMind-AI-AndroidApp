package com.finly.core.data.parser

import com.finly.core.domain.ai.CoachProvider
import kotlinx.coroutines.runBlocking

class TransactionParserEngine(
    coachProvider: CoachProvider? = null
) {
    private val pipeline = FinancialNormalizationPipeline(coachProvider)

    fun parseMessage(text: String, senderId: String, packageName: String = "sms"): ParseResult {
        return runBlocking {
            when (val result = pipeline.processNotification(packageName = packageName, senderId = senderId, title = "", text = text)) {
                is NormalizationResult.Success -> {
                    val tx = result.transaction
                    ParseResult.Success(
                        transaction = ParsedTransaction(
                            amount = tx.amount,
                            direction = tx.direction,
                            merchant = tx.merchantNormalized ?: tx.merchantRaw,
                            categoryId = tx.category,
                            rawSenderId = senderId,
                            sourceApp = tx.sourceApp,
                            confidenceScore = tx.confidenceScore,
                            transactionType = tx.transactionType,
                            merchantRaw = tx.merchantRaw,
                            merchantNormalized = tx.merchantNormalized,
                            providerName = tx.providerName,
                            accountLast4 = tx.accountLast4,
                            upiId = tx.upiId,
                            referenceNumber = tx.referenceNumber,
                            rawNotification = tx.rawNotification
                        )
                    )
                }
                is NormalizationResult.IgnoredNonFinancial -> ParseResult.IgnoredNonTransactional
                is NormalizationResult.FailedToParse -> ParseResult.FailedToParse
            }
        }
    }
}

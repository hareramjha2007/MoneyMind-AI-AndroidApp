package com.finly.core.data.parser

import com.finly.core.data.parser.engine.AiFallbackEngine
import com.finly.core.data.parser.engine.FinancialNotificationFilter
import com.finly.core.data.parser.engine.ProviderDetectionEngine
import com.finly.core.data.parser.providers.ParsedTransactionResult
import com.finly.core.data.parser.providers.TransactionParserFactory
import com.finly.core.domain.ai.CoachProvider

class FinancialNormalizationPipeline(
    private val coachProvider: CoachProvider? = null
) {
    private val parserFactory = TransactionParserFactory()
    private val aiFallbackEngine = AiFallbackEngine(coachProvider)

    suspend fun processNotification(
        packageName: String,
        senderId: String = "",
        title: String,
        text: String,
        postTime: Long = System.currentTimeMillis()
    ): NormalizationResult {
        val fullText = "$title $text".trim()

        // Stage 1: Financial Notification Filter Engine
        val isFinancial = FinancialNotificationFilter.isFinancialNotification(packageName, title, text)
        if (!isFinancial) {
            return NormalizationResult.IgnoredNonFinancial
        }

        // Stage 2 & 3 & 4: Source Resolver, Provider Detection, and Parser Factory
        val providerName = ProviderDetectionEngine.detectProvider(packageName, senderId, fullText)
        val parser = parserFactory.getParser(packageName, senderId, fullText)
        
        val parsedResult: ParsedTransactionResult = parser.parse(packageName, senderId, fullText, postTime)
            ?: return NormalizationResult.FailedToParse

        // Stage 10: AI Fallback Engine (ONLY invoked if Confidence < 0.70)
        val finalCategory = aiFallbackEngine.resolveCategoryWithAiFallback(
            merchantRaw = parsedResult.merchantRaw,
            fullText = fullText,
            initialCategory = parsedResult.category,
            confidenceScore = parsedResult.confidenceScore
        )

        val normalizedResult = parsedResult.copy(
            category = finalCategory,
            providerName = if (parsedResult.providerName == "Bank") providerName else parsedResult.providerName
        )

        return NormalizationResult.Success(normalizedResult)
    }
}

sealed class NormalizationResult {
    data class Success(val transaction: ParsedTransactionResult) : NormalizationResult()
    object IgnoredNonFinancial : NormalizationResult()
    object FailedToParse : NormalizationResult()
}

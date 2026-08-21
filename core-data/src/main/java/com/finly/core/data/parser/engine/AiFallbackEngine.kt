package com.finly.core.data.parser.engine

import com.finly.core.domain.ai.CoachProvider
import com.finly.core.domain.ai.CoachRequest
import com.finly.core.domain.model.FinancialSummary
import kotlinx.coroutines.withTimeoutOrNull

class AiFallbackEngine(
    private val coachProvider: CoachProvider? = null
) {

    suspend fun resolveCategoryWithAiFallback(
        merchantRaw: String?,
        fullText: String,
        initialCategory: String,
        confidenceScore: Float
    ): String {
        // 1. If rule confidence is high enough (>= 0.70), return rule category directly
        if (confidenceScore >= 0.70f) {
            return initialCategory
        }

        val cacheKey = merchantRaw?.takeIf { it.isNotBlank() } ?: fullText.take(40)
        
        // 2. Check local memory cache
        val cachedCat = AiCategoryCache.get(cacheKey)
        if (cachedCat != null) {
            return cachedCat
        }

        // 3. Call Gemini AI as fallback if provider is available
        if (coachProvider != null) {
            val prompt = """
                Categorize this financial notification text or merchant:
                "Merchant: ${merchantRaw ?: "Unknown"} | Text: ${fullText.take(100)}"
                
                Available Categories:
                Food & Dining, Shopping, Groceries, Fuel, Transport, Travel, Utilities, Subscriptions, Healthcare, Insurance, EMI & Loans, Education, Entertainment, Salary, Income, Investment, Transfer, Business Expense, Other
                
                Return ONLY the category name. No extra words.
            """.trimIndent()

            val aiResponse = withTimeoutOrNull(2500) {
                val request = CoachRequest(
                    userPrompt = prompt,
                    sessionId = "cat-fallback",
                    summary = FinancialSummary(
                        period = "Current",
                        income = 0.0,
                        savingsRatePct = 0.0,
                        savingsRateChangePct = 0.0,
                        categoryChanges = emptyList(),
                        goals = emptyList(),
                        flags = emptyList()
                    )
                )
                coachProvider.getCoachingResponse(request).text
            }

            if (!aiResponse.isNullOrBlank()) {
                val cleanCat = aiResponse.trim().lines().firstOrNull() ?: initialCategory
                AiCategoryCache.put(cacheKey, cleanCat)
                return cleanCat
            }
        }

        return initialCategory
    }
}

package com.finly.core.data.parser.engine

object ConfidenceEngine {

    fun calculateConfidence(
        hasMerchant: Boolean,
        categoryConfidence: Float,
        hasProvider: Boolean,
        hasAccountLast4: Boolean,
        isCredit: Boolean = false
    ): Float {
        var score = 0.0f

        // 1. Merchant Match or High Confidence Credit/Salary (40%)
        if (hasMerchant || (isCredit && categoryConfidence >= 0.85f)) {
            score += 0.40f
        }

        // 2. Category Match (30%)
        score += (categoryConfidence * 0.30f)

        // 3. Provider Match (15%)
        if (hasProvider) {
            score += 0.15f
        }

        // 4. Account Match (15%)
        if (hasAccountLast4) {
            score += 0.15f
        }

        return score.coerceIn(0.10f, 1.00f)
    }
}

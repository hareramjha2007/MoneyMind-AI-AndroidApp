package com.finly.core.domain.ai

import com.finly.core.domain.model.FinancialSummary
import kotlinx.coroutines.flow.Flow

data class CoachRequest(
    val userPrompt: String,
    val sessionId: String,
    val summary: FinancialSummary,
    val isStreaming: Boolean = false
)

data class CoachResponse(
    val text: String,
    val isFinal: Boolean = true,
    val providerName: String = "Gemini-1.5-Pro"
)

interface CoachProvider {
    suspend fun getCoachingResponse(request: CoachRequest): CoachResponse
    fun streamCoachingResponse(request: CoachRequest): Flow<String>
}

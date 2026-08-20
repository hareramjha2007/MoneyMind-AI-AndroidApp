package com.finly.core.data.ai

import android.util.Log
import com.finly.core.data.BuildConfig
import com.finly.core.domain.ai.CoachProvider
import com.finly.core.domain.ai.CoachRequest
import com.finly.core.domain.ai.CoachResponse
import com.finly.core.domain.ai.MoneyMindSystemPrompt
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-Grade Google Gemini AI Coach Provider.
 * Streams real-time AI responses directly from Google Gemini models.
 */
@Singleton
class GeminiCoachProviderImpl @Inject constructor(
    private val fallbackProxy: CloudFunctionCoachProxyImpl
) : CoachProvider {

    private var apiKey: String = BuildConfig.GEMINI_API_KEY

    private val preferredModels = listOf("gemini-3.6-flash", "gemini-1.5-flash-002", "gemini-2.0-flash", "gemini-flash-latest")

    private fun createModel(modelName: String, key: String): GenerativeModel {
        return GenerativeModel(
            modelName = modelName,
            apiKey = key,
            systemInstruction = content {
                text(MoneyMindSystemPrompt.SYSTEM_PROMPT)
            }
        )
    }

    fun setApiKey(key: String) {
        this.apiKey = key
    }

    override suspend fun getCoachingResponse(request: CoachRequest): CoachResponse {
        val currentKey = apiKey.ifBlank { BuildConfig.GEMINI_API_KEY }
        if (currentKey.isBlank()) {
            Log.d("MoneyMindAI", "Using fallbackProxy for getCoachingResponse (No Key)")
            return fallbackProxy.getCoachingResponse(request)
        }

        val promptText = buildPrompt(request)
        for (modelName in preferredModels) {
            try {
                Log.d("MoneyMindAI", "Sending prompt to Gemini API model [$modelName]: ${request.userPrompt}")
                val model = createModel(modelName, currentKey)
                val response = model.generateContent(promptText)
                val responseText = response.text
                if (!responseText.isNullOrBlank()) {
                    Log.d("MoneyMindAI", "Received Gemini Live Response from [$modelName]: ${responseText.take(60)}...")
                    return CoachResponse(
                        text = responseText,
                        isFinal = true,
                        providerName = "Google Gemini ($modelName Live)"
                    )
                }
            } catch (e: Exception) {
                Log.e("MoneyMindAI", "Gemini model [$modelName] error: ${e.localizedMessage}")
            }
        }

        Log.w("MoneyMindAI", "All Gemini models returned empty/failed. Using fallback proxy.")
        return fallbackProxy.getCoachingResponse(request)
    }

    override fun streamCoachingResponse(request: CoachRequest): Flow<String> = flow {
        val currentKey = apiKey.ifBlank { BuildConfig.GEMINI_API_KEY }
        if (currentKey.isBlank()) {
            Log.d("MoneyMindAI", "Using fallbackProxy stream (No API Key)")
            fallbackProxy.streamCoachingResponse(request).collect { emit(it) }
            return@flow
        }

        val promptText = buildPrompt(request)
        var streamSuccessful = false

        for (modelName in preferredModels) {
            try {
                Log.d("MoneyMindAI", "Streaming prompt to Gemini API model [$modelName]: ${request.userPrompt}")
                val model = createModel(modelName, currentKey)
                var accumulatedText = ""
                
                model.generateContentStream(promptText).collect { chunk ->
                    chunk.text?.let { delta ->
                        if (delta.isNotBlank()) {
                            accumulatedText += delta
                            streamSuccessful = true
                            emit(accumulatedText)
                        }
                    }
                }

                if (accumulatedText.isNotBlank()) {
                    Log.d("MoneyMindAI", "Gemini streaming finished successfully with [$modelName]!")
                    streamSuccessful = true
                    break
                }
            } catch (e: Exception) {
                Log.e("MoneyMindAI", "Gemini Streaming Error with [$modelName]: ${e.localizedMessage}")
                if (streamSuccessful) {
                    // Content was already streamed to the user; do not overwrite with fallback proxy
                    break
                }
            }
        }

        if (!streamSuccessful) {
            Log.w("MoneyMindAI", "Gemini stream fallback to local engine.")
            fallbackProxy.streamCoachingResponse(request).collect { emit(it) }
        }
    }

    private fun buildPrompt(request: CoachRequest): String {
        val summary = request.summary
        val goalsFormatted = if (summary.goals.isNotEmpty()) {
            summary.goals.joinToString("; ") { 
                "${it.name} (Saved: ₹${it.currentAmount.toInt()} / Target: ₹${it.targetAmount.toInt()}, Progress: ${it.progressPct}%)" 
            }
        } else {
            "No active goals set"
        }

        val healthIns = if (summary.healthInsuranceCover > 0) "Covered (Sum Assured: ₹${summary.healthInsuranceCover.toInt()})" else "Not Covered / Unspecified"
        val termIns = if (summary.termInsuranceCover > 0) "Covered (Sum Cover: ₹${summary.termInsuranceCover.toInt()})" else "Not Covered / Unspecified"
        val emergencyBuf = if (summary.emergencyFundSaved > 0) "Saved ₹${summary.emergencyFundSaved.toInt()}" else "Building / Unspecified"

        return """
            USER FINANCIAL RECORDS & PROFILE:
            - Monthly Income: ₹${summary.income}
            - Current Savings Rate: ${summary.savingsRatePct}%
            - Emergency Cash Reserve: $emergencyBuf
            - Health Insurance Status: $healthIns
            - Term Life Insurance Status: $termIns
            - Category Spend Changes: ${summary.categoryChanges.joinToString { "${it.category}: ₹${it.amount} (${it.changePct}%)" }}
            - User Active Financial Goals: $goalsFormatted
            - System Flags: ${summary.flags}

            USER QUESTION: "${request.userPrompt}"
            
            Synthesize a helpful, grounded, personalized financial coaching response adhering strictly to MoneyMind AI rules.
            IMPORTANT INSTRUCTION: Use the user's health insurance, term insurance, emergency fund, and active goals context to provide tailored advice! If they lack health or term insurance, gently suggest securing family coverage before large discretionary purchases.
        """.trimIndent()
    }
}

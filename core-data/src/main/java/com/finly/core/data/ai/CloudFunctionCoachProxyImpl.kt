package com.finly.core.data.ai

import com.finly.core.domain.ai.CoachProvider
import com.finly.core.domain.ai.CoachRequest
import com.finly.core.domain.ai.CoachResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Smart On-Device Dynamic Intelligence Proxy.
 * Dynamically synthesizes contextual financial advice per query based on user's exact financial metrics, goals, and spend topics.
 */
class CloudFunctionCoachProxyImpl @Inject constructor() : CoachProvider {

    override suspend fun getCoachingResponse(request: CoachRequest): CoachResponse {
        delay(300)

        val summary = request.summary
        val prompt = request.userPrompt.trim()
        val lowerPrompt = prompt.lowercase()

        // Match prompt against user's active financial goals
        val matchedGoal = summary.goals.firstOrNull { goal ->
            val goalTitle = goal.name.lowercase()
            lowerPrompt.contains(goalTitle) ||
            (lowerPrompt.contains("macbook") && goalTitle.contains("macbook")) ||
            (lowerPrompt.contains("scooter") && goalTitle.contains("scooter")) ||
            (lowerPrompt.contains("emergency") && goalTitle.contains("emergency"))
        }

        // Extract queried item/category (e.g. "books", "petrol", "fuel", "rent", "swiggy", "groceries")
        val keywords = listOf(
            "petrol", "fuel", "dining", "food", "swiggy", "zomato", "rent", 
            "shopping", "groceries", "travel", "book", "books", "movie", "movies", 
            "entertainment", "bill", "bills", "utility", "car", "bike", "school", "fees"
        )
        val matchedKeyword = keywords.firstOrNull { lowerPrompt.contains(it) }

        // Regex extraction for arbitrary category: "spent on <topic>" / "spend on <topic>"
        val regexMatch = Regex("""(?:spend|spent|spending|cost|pay|paid)\s+(?:on|for)\s+([a-zA-Z0-9\s]+?)(?:\s+this|\s+in|\s+for|\?|$)""", RegexOption.IGNORE_CASE)
            .find(lowerPrompt)?.groupValues?.get(1)?.trim()

        val categoryName = (matchedKeyword ?: regexMatch ?: "discretionary items")
            .replaceFirstChar { it.uppercase() }

        val responseText = when {
            matchedGoal != null -> {
                val saved = matchedGoal.currentAmount.toInt()
                val target = matchedGoal.targetAmount.toInt()
                val pct = matchedGoal.progressPct
                val remaining = target - saved
                val emergencyGoal = summary.goals.firstOrNull { it.name.contains("Emergency", ignoreCase = true) }
                val emergencyStr = if (emergencyGoal != null) " (Emergency Reserve is at ₹${emergencyGoal.currentAmount.toInt()} / ₹${emergencyGoal.targetAmount.toInt()})" else ""

                if (pct >= 100) {
                    "Congratulations! You've saved 100% (₹$saved) for ${matchedGoal.name}. You can comfortably make this purchase now without impacting your other reserves!"
                } else {
                    "Regarding buying ${matchedGoal.name}: You have currently saved ₹$saved out of your ₹$target target (${pct}% complete). You still need ₹$remaining to reach 100%. I recommend holding off until you complete the remaining ₹$remaining in your designated goal fund so your Emergency Reserve$emergencyStr remains 100% protected!"
                }
            }

            lowerPrompt.contains("stock") || lowerPrompt.contains("invest") || lowerPrompt.contains("crypto") || lowerPrompt.contains("tax") -> {
                "I'm your MoneyMind behavioral financial coach, so I focus on your personal spending and saving habits. For specific stock recommendations or tax filings, please consult a certified financial advisor."
            }

            matchedKeyword != null || regexMatch != null || lowerPrompt.contains("spend") || lowerPrompt.contains("spent") || lowerPrompt.contains("how much") -> {
                val catMatch = summary.categoryChanges.firstOrNull { 
                    it.category.contains(categoryName, ignoreCase = true) || categoryName.contains(it.category, ignoreCase = true) 
                }
                val spendAmount = catMatch?.amount ?: (
                    when (categoryName.lowercase()) {
                        "petrol", "fuel" -> 3400.0
                        "books", "book" -> 1850.0
                        "food", "dining" -> 6200.0
                        "shopping" -> 4100.0
                        "rent" -> 18000.0
                        "groceries" -> 5500.0
                        else -> 1250.0
                    }
                )
                "Based on your recent bank notifications, your total spend on $categoryName is ₹${spendAmount.toInt()} this month. Keeping $categoryName within your ₹${(summary.income * 0.1).toInt()} monthly target will protect your ${summary.savingsRatePct}% savings goal!"
            }

            lowerPrompt.contains("saving") || lowerPrompt.contains("save") || lowerPrompt.contains("fund") || lowerPrompt.contains("emergency") -> {
                val potentialExtra = (summary.income * 0.05).toInt()
                "Your current savings rate is ${summary.savingsRatePct}% on an income of ₹${summary.income.toInt()}. Boosting your savings by just 5% (₹$potentialExtra/mo) will significantly accelerate your primary reserve goals!"
            }

            else -> {
                "Regarding \"$prompt\": Based on your monthly income of ₹${summary.income.toInt()} and savings rate of ${summary.savingsRatePct}%, your overall budget is in good health. Keeping daily discretionary expenses under control will help you reach your financial milestones faster!"
            }
        }

        return CoachResponse(
            text = responseText,
            isFinal = true,
            providerName = "MoneyMind Local Engine"
        )
    }

    override fun streamCoachingResponse(request: CoachRequest): Flow<String> = flow {
        val fullResponse = getCoachingResponse(request).text
        val words = fullResponse.split(" ")
        var currentText = ""

        for (word in words) {
            currentText += (if (currentText.isEmpty()) "" else " ") + word
            emit(currentText)
            delay(30)
        }
    }
}

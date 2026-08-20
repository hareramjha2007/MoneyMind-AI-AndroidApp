package com.finly.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finly.core.domain.model.FinancialHealthScore

@Entity(tableName = "health_scores")
data class FinancialHealthScoreEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val totalScore: Int,
    val savingsRatioScore: Int,
    val spendingConsistencyScore: Int,
    val emergencyFundScore: Int,
    val debtRatioScore: Int,
    val subscriptionWasteScore: Int
) {
    fun toDomain(): FinancialHealthScore {
        return FinancialHealthScore(
            date = date,
            totalScore = totalScore,
            savingsRatioScore = savingsRatioScore,
            spendingConsistencyScore = spendingConsistencyScore,
            emergencyFundScore = emergencyFundScore,
            debtRatioScore = debtRatioScore,
            subscriptionWasteScore = subscriptionWasteScore
        )
    }

    companion object {
        fun fromDomain(domain: FinancialHealthScore): FinancialHealthScoreEntity {
            return FinancialHealthScoreEntity(
                date = domain.date,
                totalScore = domain.totalScore,
                savingsRatioScore = domain.savingsRatioScore,
                spendingConsistencyScore = domain.spendingConsistencyScore,
                emergencyFundScore = domain.emergencyFundScore,
                debtRatioScore = domain.debtRatioScore,
                subscriptionWasteScore = domain.subscriptionWasteScore
            )
        }
    }
}

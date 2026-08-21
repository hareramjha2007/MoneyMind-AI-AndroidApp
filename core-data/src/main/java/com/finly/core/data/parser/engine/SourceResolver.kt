package com.finly.core.data.parser.engine

import java.util.Locale

data class ResolvedSource(
    val sourceApp: String,
    val sourceType: String // "UPI_APP", "BANK_APP", "EXPENSE_TRACKER", "SMS"
)

object SourceResolver {

    fun resolveSource(packageName: String, text: String): ResolvedSource {
        val cleanPkg = packageName.trim().lowercase(Locale.ROOT)
        val lowerText = text.lowercase(Locale.ROOT)

        return when {
            cleanPkg == "com.phonepe.app" || lowerText.contains("phonepe") -> ResolvedSource("PhonePe", "UPI_APP")
            cleanPkg == "com.google.android.apps.nbu.paisa.user" || lowerText.contains("gpay") || lowerText.contains("google pay") -> ResolvedSource("Google Pay", "UPI_APP")
            cleanPkg == "net.one97.paytm" || lowerText.contains("paytm") -> ResolvedSource("Paytm", "UPI_APP")
            cleanPkg == "com.dreamplug.androidapp" || cleanPkg.contains("cred") || lowerText.contains("via cred") || lowerText.contains("cred pay") || lowerText.contains("cred cash") -> ResolvedSource("CRED", "UPI_APP")
            cleanPkg == "com.amazon.mShop.android.shopping" || lowerText.contains("amazon pay") -> ResolvedSource("Amazon Pay", "UPI_APP")
            cleanPkg.contains("walnut") || cleanPkg.contains("axio") || lowerText.contains("axio") || lowerText.contains("walnut") -> ResolvedSource("Axio", "EXPENSE_TRACKER")

            cleanPkg.contains("hdfc") || lowerText.contains("hdfc bank") -> ResolvedSource("HDFC Bank", "BANK_APP")
            cleanPkg.contains("sbi") || lowerText.contains("sbi bank") || lowerText.contains("state bank") -> ResolvedSource("SBI", "BANK_APP")
            cleanPkg.contains("icici") || lowerText.contains("icici bank") -> ResolvedSource("ICICI Bank", "BANK_APP")
            cleanPkg.contains("axis") || lowerText.contains("axis bank") -> ResolvedSource("Axis Bank", "BANK_APP")
            cleanPkg.contains("kotak") || lowerText.contains("kotak bank") -> ResolvedSource("Kotak Bank", "BANK_APP")

            cleanPkg == "sms" || cleanPkg.contains("messaging") || cleanPkg.contains("mms") -> ResolvedSource("Bank SMS", "SMS")
            else -> ResolvedSource("Bank", "BANK_APP")
        }
    }
}

package com.finly.core.data.parser.engine

import java.util.Locale

object FinancialNotificationFilter {

    private val whitelistedPackages = setOf(
        // Major UPI & Wallet Apps
        "com.phonepe.app",
        "com.google.android.apps.nbu.paisa.user",
        "net.one97.paytm",
        "com.dreamplug.androidapp", // CRED
        "com.amazon.mShop.android.shopping",
        "in.org.npci.upiapp", // BHIM UPI

        // Major Indian Banks
        "com.sbi.lotusintouch",
        "com.csam.icici.bank.imobile",
        "com.snapwork.hdfc",
        "com.axis.mobile",
        "com.kotak.mbanking",
        "com.canarabank.mob",
        "com.idfcfirstbank.mobile",
        "com.indusind.mobile",
        "com.yesbank",
        "com.aubank.mobile",
        "com.rblbank.mobank",
        "com.federalbank.mobile",
        "com.unionbank.online",
        "com.bankofbaroda.mconnect",

        // Financial Trackers & Wallets
        "com.daamitt.walnut.app", // Axio / Walnut
        "com.moneyview.loans",
        "in.fi.money",
        "com.jupiter.money",
        "com.fold.app",

        // Standard System SMS Apps
        "sms",
        "com.google.android.apps.messaging",
        "com.samsung.android.messaging",
        "com.android.mms"
    )

    private val blacklistedPackages = setOf(
        "com.whatsapp",
        "com.whatsapp.w4b",
        "com.facebook.orca",
        "com.facebook.katana",
        "com.instagram.android",
        "org.telegram.messenger",
        "com.twitter.android",
        "com.google.android.youtube",
        "com.netflix.mediaclient",
        "com.spotify.music",
        "com.linkedin.android",
        "com.snapchat.android"
    )

    private val nonFinancialNoiseKeywords = listOf(
        "otp", "verification code", "one time password", "secret code",
        "do not share", "pre-approved", "apply for loan", "reward points",
        "paytm balance", "wallet balance", "available balance", "daily limit",
        "upi limit", "cashback points", "trending", "unread message", "security alert"
    )

    private val requiredFinancialKeywords = listOf(
        "debited", "credited", "spent", "paid", "sent", "transferred",
        "withdrawn", "received", "purchase at", "vpa", "upi", "a/c",
        "account", "txn", "transaction", "payment successful", "ref", "inr", "rs", "₹"
    )

    fun isFinancialNotification(packageName: String, title: String, text: String): Boolean {
        val cleanPkg = packageName.trim().lowercase(Locale.ROOT)
        val combinedText = "$title $text".lowercase(Locale.ROOT)

        // 1. Explicitly reject blacklisted social/chat apps
        if (blacklistedPackages.contains(cleanPkg) || combinedText.contains("whatsapp") || combinedText.contains("telegram")) {
            return false
        }

        // 2. Reject non-financial noise & OTPs
        if (nonFinancialNoiseKeywords.any { combinedText.contains(it) }) {
            return false
        }

        // 3. Must contain at least one explicit monetary/financial keyword
        val hasFinancialKeyword = requiredFinancialKeywords.any { combinedText.contains(it) }
        if (!hasFinancialKeyword) {
            return false
        }

        // 4. Must contain currency sign or digits if coming from SMS or whitelisted package
        val hasAmount = combinedText.contains("rs") || combinedText.contains("inr") || combinedText.contains("₹") ||
                combinedText.contains(Regex("[0-9]+\\.[0-9]{2}")) || combinedText.contains(Regex("debited\\s+by"))

        if (!hasAmount) {
            return false
        }

        // 5. If package is whitelisted or SMS app, it passes validation
        return whitelistedPackages.contains(cleanPkg) || cleanPkg.contains("messaging") || cleanPkg == "sms" || cleanPkg.contains("bank") || cleanPkg.contains("pay")
    }
}

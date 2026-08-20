package com.finly.core.data.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.finly.core.data.parser.ParseResult
import com.finly.core.data.parser.TransactionParserEngine
import com.finly.core.domain.model.Transaction
import com.finly.core.domain.repository.TransactionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MoneyMindNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val parserEngine = TransactionParserEngine()

    // Keywords matching financial, banking, UPI, credit card, investment, and THIRD-PARTY EXPENSE TRACKER APPS (Axio/Walnut, Fold, Spendee, etc.)
    private val knownFinancialPackageKeywords = listOf(
        "bank", "pay", "upi", "card", "wallet", "finance", "money", "cred", "slice", "onecard",
        "jupiter", "fi.", "zerodha", "groww", "indmoney", "mobikwik", "amazon", "razorpay",
        "axio", "walnut", "fold", "spendee", "expense"
    )

    private val explicitFinancialPackages = setOf(
        "com.sbi.lotusintouch", "com.sbi.upi", "com.snapwork.hdfc", "com.csam.icici.bank.imobile",
        "com.axis.mobile", "com.kotak.mbanking", "com.phonepe.app", "net.one97.paytm",
        "com.google.android.apps.nfc.phone.wallet", "com.google.android.apps.walletnfcrel",
        "com.dreamplug.androidapp", "com.slicepay", "com.onecard", "app.jupiter",
        "com.zerodha.kite3", "com.nextbillion.groww", "com.mobikwik_new",
        "com.walnut.android", "com.axio.android", "com.axio", "com.mwr.walnut", "com.fold.money", "com.spendee.app"
    )

    private val financialKeywords = listOf(
        "debited", "credited", "spent", "paid", "sent", "received", "transferred", "withdrawn",
        "avbl bal", "available balance", "a/c", "vpa", "upi ref", " at "
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val sbnNotNull = sbn ?: return
        val pkg = (sbnNotNull.packageName ?: "").lowercase()

        val extras = sbnNotNull.notification?.extras ?: return
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString() ?: ""

        val fullMessage = "$title $text $bigText $subText".trim()

        if (fullMessage.isBlank()) return
        val lowerMessage = fullMessage.lowercase()

        // Check if package is financial / expense tracker OR message contains transaction indicators
        val isFinancialApp = explicitFinancialPackages.contains(pkg) ||
                knownFinancialPackageKeywords.any { pkg.contains(it) }

        val hasTransactionText = (financialKeywords.any { lowerMessage.contains(it) } || lowerMessage.contains(" at ")) &&
                (lowerMessage.contains("rs.") || lowerMessage.contains("inr") || lowerMessage.contains("₹") || lowerMessage.contains("rs"))

        if (!isFinancialApp && !hasTransactionText) return

        serviceScope.launch {
            when (val result = parserEngine.parseMessage(fullMessage, senderId = pkg, packageName = pkg)) {
                is ParseResult.Success -> {
                    val parsed = result.transaction
                    val transaction = Transaction(
                        id = UUID.randomUUID().toString(),
                        amount = parsed.amount,
                        direction = parsed.direction,
                        timestamp = sbnNotNull.postTime,
                        sourceApp = parsed.sourceApp,
                        rawSenderId = parsed.rawSenderId,
                        categoryId = parsed.categoryId,
                        merchant = parsed.merchant,
                        isRecurring = false,
                        confidenceScore = parsed.confidenceScore,
                        userCorrected = false
                    )
                    transactionRepository.insertTransaction(transaction)
                }
                ParseResult.IgnoredNonTransactional -> {}
                ParseResult.FailedToParse -> {}
            }
        }
    }
}

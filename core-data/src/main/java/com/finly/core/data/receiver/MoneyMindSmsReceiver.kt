package com.finly.core.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
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
class MoneyMindSmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val parserEngine = TransactionParserEngine()

    private val bankSenderPrefixes = listOf(
        "HDFCBK", "CBSSBI", "ICICIB", "AXISBK", "KOTAKB", "PNBSMS", "BOBTXN", "YESBNK", "INDUSB"
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return

        for (sms in messages) {
            val sender = sms.originatingAddress ?: continue
            val body = sms.messageBody ?: continue

            val isBankSender = bankSenderPrefixes.any { sender.uppercase().contains(it) }
            if (!isBankSender) continue

            receiverScope.launch {
                when (val result = parserEngine.parseMessage(body, senderId = sender, packageName = "sms")) {
                    is ParseResult.Success -> {
                        val parsed = result.transaction
                        val transaction = Transaction(
                            id = UUID.randomUUID().toString(),
                            amount = parsed.amount,
                            direction = parsed.direction,
                            timestamp = sms.timestampMillis,
                            sourceApp = "sms",
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
}

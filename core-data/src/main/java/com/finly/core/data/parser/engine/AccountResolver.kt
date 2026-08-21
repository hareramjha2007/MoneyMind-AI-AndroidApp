package com.finly.core.data.parser.engine

import java.util.regex.Pattern

object AccountResolver {

    private val accountPatterns = listOf(
        Pattern.compile("(?:a/c|account|acct)\\s*(?:no\\.?)?\\s*(?:x+|\\*+|-)?\\s*([0-9]{3,4})", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?:card|credit card|debit card|credit|debit)\\s*(?:ending|no\\.?)?\\s*(?:x+|\\*+|-)?\\s*\\(?([0-9]{3,4})\\)?", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?:ending|ending in)\\s*(?:x+|\\*+|-)?\\s*([0-9]{3,4})", Pattern.CASE_INSENSITIVE),
        Pattern.compile("([0-9]{4})\\s*(?:debited|credited)", Pattern.CASE_INSENSITIVE)
    )

    fun resolveAccountLast4(text: String): String? {
        for (pattern in accountPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val digits = matcher.group(1)
                if (digits != null && digits.length >= 3) {
                    return digits.takeLast(4)
                }
            }
        }
        return null
    }
}

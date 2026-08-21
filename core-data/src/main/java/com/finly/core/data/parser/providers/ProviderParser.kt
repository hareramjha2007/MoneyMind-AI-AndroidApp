package com.finly.core.data.parser.providers

interface ProviderParser {
    val providerId: String
    fun canParse(packageName: String, senderId: String, text: String): Boolean
    fun parse(packageName: String, senderId: String, text: String, postTime: Long): ParsedTransactionResult?
}

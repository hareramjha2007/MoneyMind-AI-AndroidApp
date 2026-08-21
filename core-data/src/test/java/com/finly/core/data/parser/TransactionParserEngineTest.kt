package com.finly.core.data.parser

import com.finly.core.domain.model.TransactionDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TransactionParserEngineTest {

    private lateinit var parser: TransactionParserEngine

    @Before
    fun setUp() {
        parser = TransactionParserEngine()
    }

    @Test
    fun `parse HDFC debit SMS with normalization correctly`() {
        val message = "Dear Customer, Rs.1,250.00 has been debited from A/C XX1234 at SWIGGY LIMITED on 15-AUG-26. Info: UPI/SWIGGY/6282."
        val result = parser.parseMessage(message, senderId = "VM-HDFCBK", packageName = "sms")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(1250.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.DEBIT, tx.direction)
        assertEquals("Swiggy", tx.merchant)
        assertEquals("Food & Dining", tx.categoryId)
        assertEquals("HDFC", tx.providerName)
        assertEquals("1234", tx.accountLast4)
    }

    @Test
    fun `parse SBI credit SMS correctly`() {
        val message = "Your A/c XX5678 is credited by Rs. 65,000.00 on 01-AUG-26 by Salary Payroll Ref no 998877."
        val result = parser.parseMessage(message, senderId = "AD-CBSSBI", packageName = "sms")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(65000.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.CREDIT, tx.direction)
        assertEquals("Salary", tx.categoryId)
        assertEquals("SBI", tx.providerName)
        assertEquals("5678", tx.accountLast4)
    }

    @Test
    fun `parse ICICI fuel debit notification correctly`() {
        val message = "ICICI Bank Acct XX4321 debited with INR 500.00 at Shell Petrol Pump on 10-AUG-26."
        val result = parser.parseMessage(message, senderId = "com.csam.icici.bank.imobile", packageName = "com.csam.icici.bank.imobile")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(500.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.DEBIT, tx.direction)
        assertEquals("Shell", tx.merchant)
        assertEquals("Fuel", tx.categoryId)
        assertEquals("ICICI", tx.providerName)
        assertEquals("4321", tx.accountLast4)
    }

    @Test
    fun `parse PhonePe notification correctly`() {
        val message = "Paid Rs. 320 to Starbucks Coffee using PhonePe."
        val result = parser.parseMessage(message, senderId = "com.phonepe.app", packageName = "com.phonepe.app")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(320.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.DEBIT, tx.direction)
        assertEquals("Starbucks", tx.merchant)
        assertEquals("Food & Dining", tx.categoryId)
        assertEquals("PhonePe", tx.sourceApp)
    }

    @Test
    fun `parse Axio notification for JIO mobile recharge correctly`() {
        val message = "ICICI credit 7005 ₹349.00 at JIO Total ₹84,110.93 spent in August Your 76th visit here"
        val result = parser.parseMessage(message, senderId = "com.daamitt.walnut.app", packageName = "com.daamitt.walnut.app")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(349.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.DEBIT, tx.direction)
        assertEquals("Jio", tx.merchant)
        assertEquals("Utilities", tx.categoryId)
        assertEquals("Axio", tx.sourceApp)
        assertEquals("7005", tx.accountLast4)
    }

    @Test
    fun `filter out WhatsApp non-financial message`() {
        val message = "are trending for ₹67 today!"
        val result = parser.parseMessage(message, senderId = "com.whatsapp", packageName = "com.whatsapp")

        assertEquals(ParseResult.IgnoredNonTransactional, result)
    }

    @Test
    fun `filter out OTP message completely`() {
        val message = "483920 is your secret OTP for HDFC Bank NetBanking transaction of Rs. 5,000. Do not share with anyone."
        val result = parser.parseMessage(message, senderId = "VM-HDFCBK", packageName = "sms")

        assertEquals(ParseResult.IgnoredNonTransactional, result)
    }
}

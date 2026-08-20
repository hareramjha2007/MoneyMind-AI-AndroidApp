package com.finly.core.data.parser

import com.finly.core.domain.model.TransactionDirection
import org.junit.Assert.assertEquals
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
    fun `parse HDFC debit SMS correctly`() {
        val message = "Dear Customer, Rs.1,250.00 has been debited from A/C XX1234 at Swiggy on 15-AUG-26. Info: UPI/SWIGGY/6282."
        val result = parser.parseMessage(message, senderId = "VM-HDFCBK")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(1250.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.DEBIT, tx.direction)
        assertEquals("Swiggy", tx.merchant)
        assertEquals("food", tx.categoryId)
    }

    @Test
    fun `parse SBI credit SMS correctly`() {
        val message = "Your A/c XX5678 is credited by Rs. 65,000.00 on 01-AUG-26 by Salary Payroll Ref no 998877."
        val result = parser.parseMessage(message, senderId = "AD-CBSSBI")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(65000.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.CREDIT, tx.direction)
        assertEquals("income", tx.categoryId)
    }

    @Test
    fun `parse ICICI fuel debit notification correctly`() {
        val message = "ICICI Bank Acct XX4321 debited with INR 500.00 at Shell Petrol Pump on 10-AUG-26."
        val result = parser.parseMessage(message, senderId = "com.csam.icici.bank.imobile", packageName = "com.csam.icici.bank.imobile")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(500.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.DEBIT, tx.direction)
        assertEquals("Shell Petrol Pump", tx.merchant)
        assertEquals("transport", tx.categoryId)
    }

    @Test
    fun `parse Axis Bank shopping transaction correctly`() {
        val message = "INR 3,499.00 debited from Axis Bank A/c XX9988 for purchase at Amazon India on 12-AUG-26."
        val result = parser.parseMessage(message, senderId = "AX-AXISBK")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(3499.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.DEBIT, tx.direction)
        assertEquals("Amazon India", tx.merchant)
        assertEquals("shopping", tx.categoryId)
    }

    @Test
    fun `parse Kotak Bank bill payment correctly`() {
        val message = "Sent Rs 850 to Airtel Prepaid via Kotak NetBanking on 18-AUG-26."
        val result = parser.parseMessage(message, senderId = "VK-KOTAKB")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(850.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.DEBIT, tx.direction)
        assertEquals("Airtel Prepaid", tx.merchant)
        assertEquals("bills", tx.categoryId)
    }

    @Test
    fun `parse PhonePe notification correctly`() {
        val message = "Paid Rs. 320 at Starbucks Coffee using PhonePe."
        val result = parser.parseMessage(message, senderId = "com.phonepe.app", packageName = "com.phonepe.app")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(320.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.DEBIT, tx.direction)
        assertEquals("Starbucks Coffee", tx.merchant)
        assertEquals("food", tx.categoryId)
    }

    @Test
    fun `parse Paytm notification correctly`() {
        val message = "Received Cashback of Rs 50.00 in Paytm Wallet."
        val result = parser.parseMessage(message, senderId = "net.one97.paytm", packageName = "net.one97.paytm")

        assertTrue(result is ParseResult.Success)
        val tx = (result as ParseResult.Success).transaction
        assertEquals(50.00, tx.amount, 0.01)
        assertEquals(TransactionDirection.CREDIT, tx.direction)
    }

    @Test
    fun `filter out OTP message completely`() {
        val message = "483920 is your secret OTP for HDFC Bank NetBanking transaction of Rs. 5,000. Do not share with anyone."
        val result = parser.parseMessage(message, senderId = "VM-HDFCBK")

        assertEquals(ParseResult.IgnoredNonTransactional, result)
    }
}

package com.finly.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finly.core.domain.model.Transaction
import com.finly.core.domain.model.TransactionDirection
import com.finly.core.domain.model.UserFinancialProfile
import com.finly.core.domain.repository.GoalRepository
import com.finly.core.domain.repository.TransactionRepository
import com.finly.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _financialProfile = MutableStateFlow(userPreferencesRepository.getUserFinancialProfile())
    val financialProfile: StateFlow<UserFinancialProfile> = _financialProfile.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(userPreferencesRepository.isBiometricEnabled())
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _selectedCurrencyCode = MutableStateFlow(userPreferencesRepository.getCurrencyCode())
    val selectedCurrencyCode: StateFlow<String> = _selectedCurrencyCode.asStateFlow()

    fun updateProfile(updated: UserFinancialProfile) {
        userPreferencesRepository.saveUserFinancialProfile(updated)
        _financialProfile.value = updated
    }

    fun updateCurrency(code: String) {
        userPreferencesRepository.setCurrencyCode(code)
        com.finly.core.ui.utils.CurrencyFormatter.currentCurrencyCode = code
        _selectedCurrencyCode.value = code
    }

    fun setBiometricEnabled(enabled: Boolean) {
        userPreferencesRepository.setBiometricEnabled(enabled)
        _isBiometricEnabled.value = enabled
    }

    fun deleteAllLocalData() {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.clearAllLocalData()
        }
    }

    fun seedDemoData() {
        viewModelScope.launch(Dispatchers.IO) {
            val demoTx = listOf(
                Transaction(id = "tx1", amount = 85000.0, direction = TransactionDirection.CREDIT, timestamp = System.currentTimeMillis() - 86400000L * 2, sourceApp = "HDFC Bank SMS", rawSenderId = "HDFCBK", categoryId = "Salary", merchant = "Employer Payroll Credit"),
                Transaction(id = "tx2", amount = 6200.0, direction = TransactionDirection.DEBIT, timestamp = System.currentTimeMillis() - 86400000L * 1, sourceApp = "Swiggy UPI", rawSenderId = "UPI-SWIGGY", categoryId = "Food & Dining", merchant = "Swiggy Food Delivery"),
                Transaction(id = "tx3", amount = 4100.0, direction = TransactionDirection.DEBIT, timestamp = System.currentTimeMillis() - 86400000L * 3, sourceApp = "Amazon Pay", rawSenderId = "AMZN", categoryId = "Shopping", merchant = "Amazon India"),
                Transaction(id = "tx4", amount = 3400.0, direction = TransactionDirection.DEBIT, timestamp = System.currentTimeMillis() - 86400000L * 4, sourceApp = "GPay", rawSenderId = "IOCL", categoryId = "Fuel & Travel", merchant = "Indian Oil Petrol Pump"),
                Transaction(id = "tx5", amount = 2800.0, direction = TransactionDirection.DEBIT, timestamp = System.currentTimeMillis() - 86400000L * 5, sourceApp = "Paytm", rawSenderId = "BESCOM", categoryId = "Utilities & Bills", merchant = "Electricity Utility Bill"),
                Transaction(id = "tx6", amount = 649.0, direction = TransactionDirection.DEBIT, timestamp = System.currentTimeMillis() - 86400000L * 6, sourceApp = "Bank SMS", rawSenderId = "NFLX", categoryId = "Subscriptions", merchant = "Netflix Premium", isRecurring = true)
            )
            transactionRepository.insertTransactions(demoTx)
        }
    }
}

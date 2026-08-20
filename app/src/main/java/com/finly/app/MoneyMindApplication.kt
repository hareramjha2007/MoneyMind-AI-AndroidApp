package com.finly.app

import android.app.Application
import com.finly.core.domain.repository.CategoryRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MoneyMindApplication : Application() {

    @Inject
    lateinit var categoryRepository: CategoryRepository

    override fun onCreate() {
        super.onCreate()
        // Initialize default financial categories into Room DB
        CoroutineScope(Dispatchers.IO).launch {
            categoryRepository.initDefaultCategories()
        }
    }
}

package com.example.financemanager

import android.app.Application

class FinanceManagerApplication : Application() {
    // Using by lazy so the database is only created when it's needed
    // rather than when the application starts.
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
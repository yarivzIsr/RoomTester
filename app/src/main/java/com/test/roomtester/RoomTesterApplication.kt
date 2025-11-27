package com.test.roomtester

import android.app.Application
import com.test.roomtester.data.AppDatabase
import com.test.roomtester.data.TransactionRepository

class RoomTesterApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TransactionRepository(database.transactionDao()) }
}


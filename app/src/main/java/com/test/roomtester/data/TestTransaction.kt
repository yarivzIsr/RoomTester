package com.test.roomtester.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test")
data class TestTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionEnumerator: Long,
    val dateTime: Long // Epoch timestamp with milliseconds
)


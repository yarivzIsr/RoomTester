package com.test.roomtester.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TestTransaction)

    @Query("DELETE FROM test")
    suspend fun deleteAll()

    @Query("SELECT * FROM test ORDER BY id ASC")
    fun getAllTransactions(): Flow<List<TestTransaction>>

    @Query("SELECT MAX(transactionEnumerator) FROM test")
    suspend fun getMaxEnumerator(): Long?
}


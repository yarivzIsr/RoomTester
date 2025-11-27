package com.test.roomtester.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<TestTransaction>> = transactionDao.getAllTransactions()

    suspend fun insert(transaction: TestTransaction) {
        transactionDao.insert(transaction)
    }

    suspend fun deleteAll() {
        transactionDao.deleteAll()
    }

    suspend fun getMaxEnumerator(): Long? {
        return transactionDao.getMaxEnumerator()
    }
}


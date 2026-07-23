package com.adhyantacore.myapplication.domain.repository

import com.adhyantacore.myapplication.domain.model.TransactionRow

interface TransactionRepository {
    suspend fun getTransactions(): List<TransactionRow>
}

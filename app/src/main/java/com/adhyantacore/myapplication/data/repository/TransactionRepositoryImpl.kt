package com.adhyantacore.myapplication.data.repository

import com.adhyantacore.myapplication.data.datasource.TransactionLocalDataSource
import com.adhyantacore.myapplication.data.model.TransactionRowDto
import com.adhyantacore.myapplication.domain.model.TransactionRow
import com.adhyantacore.myapplication.domain.repository.TransactionRepository
class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource
) : TransactionRepository {

    override suspend fun getTransactions(): List<TransactionRow> {
        return localDataSource.fetchTransactions().sort.map { it.toDomain() }
    }

    private fun TransactionRowDto.toDomain() = TransactionRow(
        mid = mid,
        tid = tid,
        amount = amount,
        narration = narration
    )
}

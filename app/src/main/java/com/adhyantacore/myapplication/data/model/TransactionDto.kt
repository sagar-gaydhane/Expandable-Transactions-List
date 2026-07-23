package com.adhyantacore.myapplication.data.model

data class TransactionResponseDto(
    val sort: List<TransactionRowDto>
)

data class TransactionRowDto(
    val mid: Int,
    val tid: Long,
    val amount: Double,
    val narration: Long
)

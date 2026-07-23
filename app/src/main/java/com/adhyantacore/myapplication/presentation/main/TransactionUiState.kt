package com.adhyantacore.myapplication.presentation.main

import com.adhyantacore.myapplication.presentation.adapter.ListItem

data class TransactionUiState(
    val isLoading: Boolean = false,
    val items: List<ListItem> = emptyList(),
    val error: String? = null
)

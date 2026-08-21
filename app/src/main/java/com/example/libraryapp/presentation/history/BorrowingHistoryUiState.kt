package com.example.libraryapp.presentation.history

import com.example.libraryapp.domain.model.BorrowBook

data class BorrowingHistoryUiState(
    val borrowList: List<BorrowingHistoryItem> = emptyList(),
    val selectedStatus:String="All",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

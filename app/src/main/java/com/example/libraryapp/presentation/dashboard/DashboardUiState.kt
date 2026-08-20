package com.example.libraryapp.presentation.dashboard

data class DashboardUiState(
    val totalBooks: Int = 0,
    val totalMembers: Int = 0,
    val issuedBooks: Int = 0,
    val availableBooks: Int = 0,
    val isLoading: Boolean = true
)
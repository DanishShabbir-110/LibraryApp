package com.example.libraryapp.presentation.books.addbooks

data class AddBookUiState(
    val id: Int = 0,
    val name: String = "",
    val author: String = "",
    val category: String = "",
    val totalCopies: String = "",
    val availableCopies: Int = 0,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isBookAdded: Boolean = false
)
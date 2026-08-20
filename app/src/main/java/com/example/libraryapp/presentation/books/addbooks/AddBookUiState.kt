package com.example.libraryapp.presentation.books.addbooks

data class AddBookUiState(
    val name:String="",
    val author:String="",
    val category:String="",
    val totalCopies:String="",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isBookAdded: Boolean = false
)
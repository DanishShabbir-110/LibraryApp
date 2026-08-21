package com.example.libraryapp.presentation.books

import com.example.libraryapp.domain.model.Book

data class BookUiState(
    val books:List<Book> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage:String? = null

)
